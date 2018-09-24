# Inspiration
Supermarkets and grocery stores all across the world are built to be confusing. They hide the most necessary items in the farthest corners, all in the attempt of trying to lure buyers into buying other products. That's where Markt comes in.

# What it does
By implementing various APIs and algorithms, Markt generates a supermarket layout bitmap and also animates the path that the consumer should take to minimize the time spent at the store and to pick up all the groceries on their list.

# How Markt works
## Bitmap
Actual store layouts were hard to find in the time-constraints given, so I created a mock bitmap using excel with 0's representing free space and 1's representing obstacles such as aisles, cashier counters etc. I then translated this into a bitmap of the whole store layout in Java.

## Pathfinding Algorithms
Markt uses an existing database from Supermarket API: when given a product name, it returns the aisle number and other product information based on models from prominent retailers across the world. Using information about the location such as "Aisle 11 Back," Markt is then able to plot an exact coordinate on the bitmap that fills within the bounds of "Aisle 11 Back." After adding all the user's products to the list and logging all the bitmap coordinates, the challenge then became finding the most optimal route between all the points. This is essentially a different use case of the Traveling Salesman Problem except that the starting and ending point are essentially the same, so using a slightly modified Dijkstra's algorithm I calculated the most efficient route. Because the algorithm did not account for obstacles between the positions of the products, I also implemented the A* algorithm on the bitmap matrix to find the best path between the individual points to avoid aisle and other obstacles.

## Firebase and UI
After adding an item to the grocery list and clicking the start button to map the path, the Supermarket API searches for that item's aisle number. The aisle number is a key for the possible coordinates within that aisle and a "best fit" position is returned. A major part of this app was designing the UI. In 8bit, even though the animation was fully functional, it was hard to interpret the store layout and where the user needed to go. I added numerous features for clarification, such as animating the current path to the closest item, adding a pulse for the next item in progress, and varying the sizes of the item points.

# Challenges I ran into
My biggest challenge was definitely implementing pathfinding algorithms. At the beginning of designing Markt, I had no background and minimal understanding of pathfinding algorithms outside of Dijkstra, so understanding how exactly A* and TSP worked and how to integrate them took a majority of my time. In addition, I spent at least 40% of my time designing the UI. Graphically representing the store layout and constructing the actual bitmap took a lot of time as well.

# Accomplishments that I'm proud of
The app is fully functional (and scalable to multiple real-life supermarket layouts).

# What I learned
Honestly more than I could have ever imagined. Although I'm proficient at Java, I had very minimal knowledge of Android Studio going in, so just finishing this project is a plus. Integrating Firebase into an Android app for the first time was also a fun experience, and so was learning the nooks and crannies of A* and TSP!

# What's next for Markt
Providing real-time cross-platform list integration for ease of user access, and potentially adding a built-in social media platform for Markt consumers and retailers. More importantly, finding a database of real store layouts and writing algorithms (ML, AI, etc.) to efficiently convert those layouts into clean, effective bitmaps.

Made with ♥ in 2018
