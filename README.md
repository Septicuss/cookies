# Cookies

This plugin was created as part of a code challenge. The task was:
- to implement a basic cookie clicker tied to a block
- clicking the block opens a cookie clicked GUI
- data has to be persisted using SQL

## Results

**Libraries used**
- **packetevents** - *to handle updating inventory titles using JSON*
- **mjson** - *minimalist library to handle JSON, used to serialize cookie data*
- **sqlite-jdbc** - *SQLite driver*
- **custom-block-data** by mfnalex - *simple Spigot library to handle keeping custom data in chunks for blocks*

**Details**
- Players can retrieve a cookie block using `/cookies get`
- Once placed, the cookie block becomes unique and gets its own ID
- Breaking or damaging the block in any way will drop an item, which contains the now-unique ID.
- Right-clicking the block opens a dynamic GUI, with the title being used to display the resource pack based interface.
- Title and item updates are handled by intercepting, modifying and sending window packets using **packetevents**.
- Saved using a key-value store, with the key being a UUID and value being JSON. Currently using SQLite, but extensibility through an interface is possible.

## Screenshot

![image](https://github.com/user-attachments/assets/09f65bc4-6fbd-4f54-b264-0f05b7659ae4)
