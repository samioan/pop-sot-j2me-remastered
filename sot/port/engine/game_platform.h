// Classes of Prince of Persia: The Sands of Time that are written by hand instead of translated:
// the MIDlet (startApp creates the canvas and its thread, which the host replaces).
#pragma once

namespace pop {

struct Game;

struct PrinceSOT : MIDlet {
    static inline Ref<PrinceSOT> instance{};
    static inline Ref<Game> c{};            // the canvas
    void destroyApp(jboolean unconditional); // engine_glue.cpp: saves through Game.n()
};

} // namespace pop
