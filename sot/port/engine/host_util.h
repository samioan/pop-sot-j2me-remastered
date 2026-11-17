// Small platform-independent helpers shared by the game host and the launcher: SHA-256, the JAR's
// central directory, and the identity of the supported JAR. Nothing here depends on the engine.
#pragma once
#include <cstddef>
#include <cstdint>
#include <string>
#include <vector>

namespace pophost {

std::string sha256Hex(const std::vector<std::uint8_t>& data);

struct ZipEntry {
    std::string name;
    std::vector<std::uint8_t> data;
};

// One file in a ZIP/JAR's central directory, with where its (possibly compressed) bytes start.
struct ZipDirEntry {
    std::string name;
    unsigned method = 0;            // 0 stored, 8 deflated
    std::size_t compressed = 0, uncompressed = 0, dataStart = 0;
};

// Lists the file entries (not directories) of a ZIP/JAR image, checking every offset against its size.
bool readZipDirectory(const std::vector<std::uint8_t>& archive, std::vector<ZipDirEntry>& out, std::string& error);

// Reads every stored/deflated file entry from a ZIP/JAR image (host_zip.cpp, with inflate.cpp
// inflater). Returns false on a malformed archive.
bool readZip(const std::vector<std::uint8_t>& archive, std::vector<ZipEntry>& out, std::string& error);

// Identity of the JAR this executable was built for: Gameloft's Prince of Persia: The Sands of Time
// for Nokia Series 60 (176x208), MIDlet 1.1.0.
constexpr const char* kExpectedJarSha256 = "40e7115a32e9e584106bc6565ed78cd6af189b41a426f5d61f53bbf8d0ac3e9f";
constexpr const char* kExpectedJarName = "Prince of Persia: The Sands of Time 1.1.0 for Nokia Series 60, 176x208 (PrinceOfPersia-TheSandsOfTime.jar)";
constexpr const wchar_t* kUserFolder = L"PrinceOfPersiaSoT";
// What the MIDlet and the handset report (manifest values; the platform the game expects).
constexpr const char* kMidletVersion = "1.1.0";
constexpr const char* kMidletName = "PrinceSOT";
constexpr const char* kPlatform = "Nokia7650"; // the default key layout (Game.h = 0)

// Files the engine opens (level, sprite and sound banks, images). Used to validate a data directory.
const std::vector<std::string>& requiredDataFiles();

} // namespace pophost
