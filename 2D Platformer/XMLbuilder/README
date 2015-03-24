Author: Diana Ly
CS420 - Game Engineering

README 

This program converts a text file into an XML file and is created in such 
a way that it can only be used to create levels for our Project 2 only.  

			--DIRECTIONS-- 
To use, open the text file you want to convert. The program will output 
the path in which the XML file is saved. Please read IMPORTANT NOTES  
for directions on how to format the text file so the program will be 
able to read and recognize the characters.  

		     --IMPORTANT NOTES--
Our current game window size is 960 x 640. The correlation of the 
blocks/characters is 15 x 10 respectively, meaning that when creating 
the width of the game, you would want to use multiples of 15 and that 
you would want to keep the height to exactly 10 characters. 

Use the legend below so you know which characters represent which 
blocks. 
	         ---------LEGEND --------
       	         | x = normal blocks    | 
       	         | c = cake             |
	         | t = trap blocks      |			       
       	         | o = breakable blocks |
                 | b = bouncy blocks    |
                 | u = unstable blocks  | 
                 | s = saw blocks       |
                 | k = key blocks       |
                 | m = lock blocks      |
 	         ------------------------

Any character not stated above represents a space. Please do not include 
spaces in your text file as the program will not recognize a space is a space. 

For the very first lines of the text file, include the tile names for the 
texture following in this order: 
normal blocks
bounce blocks
trap blocks
breakable blocks
unstable blocks
saw blocks
lock blocks
key blocks
cake blocks

Example of the text file (15x10 -- 960 x 640 size):
 
cubeTexture
bounceTexture
trapTexture
breakableTexture
unstableTexture
sawTexture
lockTexture
keyTexture
cakeTexture
---------------
---------------
---------------
---------------
----s---xxxu---
--------x------
--------xoo----
--x-----x-----m
--x----bxk----c
xxxxxxxxxxxxxtt
  
  		  --MORE NOTES ON THE MATH CONVERSION--
As stated above, this is the current conversion from our tiles to our game window:
		             (width x height) 
		  characters/blocks   game window 
                          (15 x 10) = (960 x 640) 
                          
If you want a bigger map, repeat the width so the math will be simple for the 
setBoundaries method of the code.

Example: In the LevelTester2 in Project 2, I made it so it's (960*5 x 640) so 
the maximum boundary size of the map is 4800 x 640.
