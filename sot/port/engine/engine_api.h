// Host-facing interface of the translated engine. Deliberately free of generated headers so the
// platform layer (and <windows.h>) never meets the game's identifiers.
#pragma once
#include <cstdint>
#include <string>
#include <vector>

namespace popapi {

// Nokia key codes the game reads in keyPressed/keyReleased.
enum Key : int {
    KeyUp = -1, KeyDown = -2, KeyLeft = -3, KeyRight = -4, KeyFire = -5, KeySoftLeft = -6, KeySoftRight = -7,
    Key0 = 48, Key1, Key2, Key3, Key4, Key5, Key6, Key7, Key8, Key9, KeyStar = 42, KeyPound = 35
};

struct Frame {
    const std::uint32_t* pixels = nullptr; // 0xAARRGGBB, top-down
    int width = 0, height = 0;
};

// Values the port patches read (tools/port_java/PortConfig.java). Vanilla builds ignore them.
// The original handset's screen; the port widens it.
constexpr int kBaseWidth = 176, kBaseHeight = 208;

struct Options {
    int screenWidth = kBaseWidth; // logical width; height is always kBaseHeight
    bool interpolation = false; // record draw calls so renderInterpolated can present between ticks
    bool optionsMenu = false;   // add the OPTIONS entry to the game's menus (set before start)
    bool inputBuffer = false;   // PC input buffer (PortConfig.inputTick): changes play, so off for the checker
};

void configure(const Options& options);
void start(std::int64_t randomSeed, bool fixedSeed);
void tick();                   // one original loop iteration: Game.d (update) and Game.paint
double tickSeconds();          // length of the next iteration: Game.H (80 ms in play)
Frame frame();
void keyPressed(int midpKey);
void keyReleased(int midpKey);
void hideNotify();
void showNotify();
bool finished();               // the game reached its exit state
void shutdown();               // MIDlet.destroyApp: flush the save record

// Introspection used by the host UI, tests and the equivalence checker.
int gameState();
int levelIndex();
bool inGameplay();
// 0 = menus/prompts, 1 = free gameplay, 2 = gameplay with a message or hint open.
int inputContext();
std::uint64_t stateHash();
std::string describeState();
void dumpState(std::vector<std::string>& lines);

// Render-only interpolation support (port builds).
bool renderInterpolated(double alpha);

// "size style height ascent w0..w255" per line, for the oracle's Font (after start()).
std::string fontMetrics();

} // namespace popapi
