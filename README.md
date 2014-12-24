## Power Grid Engineer - Android
This Android application calculates the shortest route between cities based on the game board for Power Grid.

By going to 'Settings' in the action bar, a user can set which side of the board they're using and which regions they're currently playing in. This will change which cities appear in the drop downs on the main screen.

A list of recent calculations is at the bottom half of the screen. This list can be cleared by hitting the trash can icon.

### Database (and Board) Info
Either country (US or Germany) contains 6 colored regions.

Each region contains 7 cities --> each country has 42 cities

--> Total 84 cities.

On the US side, there are a total of 87 paths between cities.

On the German side, there are a total of 83 connections between cities.

If city A is connected to city B, then B is also connected to A, so you must have 2 entries for each city connection.

So the US side has 174 paths, the German side has 164.

--> Total 340 paths.