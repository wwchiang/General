using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;
using System.IO;

namespace Project3
{
    /*
     * Notes: Maps are generated via text files and are found in the Maps folder of Content.
     * 
     * More examples can be seen in the text files.
     * 
     * Current legend: 
     * 
     * d = dirt tile
     * x = battle tile [difficulty == hard. texture == cave tile] 
     * y = battle tile [difficulty == medium. texture == cave tile]
     * z = battle tile [difficulty == easy. texture = grass tile]
     * t = transition tile to get into the caves 
     * v = transition tile to get out of the caves 
     * g = grass tile 
     * h = tree tile 
     * w = log tile
     * c = cave tiles 
     * f/e = fence tiles (begin/end respectively)
     * k = water tile 
     * 
     * If you add more tiles, update this and the MapTile class:
     *          What to update here: 
     *              - Create another Texture2D
     *              - Load it in LoadContent method 
     *              - Give a name for it 
     *              - Go to GenerateMap method 
     *              - Add a case character for it and follow the pattern in creating the mapTile 
     *              - When creating the MapTile, make sure you adjust its properties by its type
     *          What to update in MapTile class if applicable 
     *              - If it's a different transition tile, specify which map it transitions to 
     *                by adjusting its transitionTo attribute and define it by its name 
     *              - If it's an interactable tile, specify its attributes by its name 
     *              - TBC when we have more tiles.
     * 
     */
    public class Map
    {
        public Game1 game;
        
        /* Size of each map */
        public int width {get; private set;}
        public int height { get; private set; }

        /* Map Textures */
        private Texture2D grassText;
        private Texture2D dirtText;
        private Texture2D redTransition;
        private Texture2D blueTransition;
        private Texture2D transition;
        private Texture2D caveTile;
        private Texture2D easy;
        private Texture2D medium;

        /* Map Texture Objects */
        private Texture2D rockTile;
        private Texture2D treeText;
        private Texture2D woodText;
        private Texture2D fenceText;
        private Texture2D fenceEnd;
        private Texture2D waterText;
        private Texture2D houseText1;
        private Texture2D houseText2;
        private Texture2D houseText3;
        private Texture2D houseText4;

        private Texture2D easyplains;
        private Texture2D mediumforest;
        private Texture2D grass1;
        private Texture2D grass2;
        private Texture2D grass3;

        public Maptile[,] currentMap { get; private set; }

        public Map(Game1 g)
        {
            game = g;
        }

        public void LoadContent()
        {
            /* Loads textures */
            grassText = game.Content.Load<Texture2D>("MapTexture/grass");
            dirtText = game.Content.Load<Texture2D>("MapTexture/dirt");
            caveTile = game.Content.Load<Texture2D>("MapTexture/cavetexture");

            redTransition = game.Content.Load<Texture2D>("MapTexture/hard");
            medium = game.Content.Load<Texture2D>("MapTexture/forest");
            easy = game.Content.Load<Texture2D>("MapTexture/plains");
            
            blueTransition = game.Content.Load<Texture2D>("MapTexture/caveopening");
            transition = game.Content.Load<Texture2D>("MapTexture/caveopening2");
            
            treeText = game.Content.Load<Texture2D>("MapTexture/treetexture");
            woodText = game.Content.Load<Texture2D>("MapTexture/wood");
            rockTile = game.Content.Load<Texture2D>("MapTexture/rock");
            fenceEnd = game.Content.Load<Texture2D>("MapTexture/fenseend");
            fenceText = game.Content.Load<Texture2D>("MapTexture/fencebegin");
            waterText = game.Content.Load<Texture2D>("MapTexture/water");
            
            houseText1 = game.Content.Load<Texture2D>("MapTexture/house1");
            houseText2 = game.Content.Load<Texture2D>("MapTexture/house2");
            houseText3 = game.Content.Load<Texture2D>("MapTexture/house3");
            houseText4 = game.Content.Load<Texture2D>("MapTexture/house4");

            easyplains = game.Content.Load<Texture2D>("MapTexture/plains");
            mediumforest = game.Content.Load<Texture2D>("MapTexture/forest");

            grass1 = game.Content.Load<Texture2D>("MapTexture/grass1");
            grass2 = game.Content.Load<Texture2D>("MapTexture/grass2");
            grass3 = game.Content.Load<Texture2D>("MapTexture/grass3");
            /* Gives textures names used to different special attributes if applicable */
            redTransition.Name = "hard";
            easy.Name = "easy";
            medium.Name = "medium";
            blueTransition.Name = "transition";
            transition.Name = "return";
        }

        public void GenerateMap (int i)
        {
            Random r = new Random();
            List<string> lines = new List<string>();

            Stream stream = TitleContainer.OpenStream("Content/Maps/test" + i + ".txt");
            using (StreamReader reader = new StreamReader(stream))
            {
                string line = reader.ReadLine();
                width = line.Length;
                while (line != null)
                {
                    lines.Add(line);
                    line = reader.ReadLine();
                }
            }

            height = lines.Count;

            currentMap = new Maptile[width, height];

            Maptile newTile;

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    char tileType = lines[y][x];
                    
                    switch (tileType)
                    
                    {
                        // If more tiles are added, give a case letter and create the tiles with the appropriate attributes 
                        case 'd':
                            /* Creates a dirt tile*/
                            newTile = new Maptile(dirtText, new Vector2(x, y), false, false, false, false );                            
                            currentMap[x, y] = newTile;
                            break;
                        case 'x':
                            /* Creates a transition tile to battle automagically [hard] */
                            newTile = new Maptile(redTransition, new Vector2(x, y), false, false, true, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'y':
                            /* Creates a transition tile to battle automagically [medium] */
                            newTile = new Maptile(medium, new Vector2(x, y), false, false, true, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'z':
                            /* Creates a transition tile to battle automagically [easy] */
                            newTile = new Maptile(easy, new Vector2(x, y), false, false, true, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 't':
                            /* Creates a normal transition tile to a different map containing dirt */
                            newTile = new Maptile(blueTransition, new Vector2(x, y), false, true, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'g':
                            /* Creates a grass tile */
                            int randy = r.Next(10);
                            if (randy > 0 && randy < 4)
                            {
                                newTile = new Maptile(grass1, new Vector2(x, y), false, false, false, false);
                            }
                            else if (randy >= 4 && randy < 7)
                            {
                                newTile = new Maptile(grass2, new Vector2(x, y), false, false, false, false);
                            }
                            else
                            {
                                newTile = new Maptile(grass3, new Vector2(x, y), false, false, false, false);
                            }
                            currentMap[x, y] = newTile;
                            break;
                        case 'h':
                            /* Creates a tree on grass tile */
                            newTile = new Maptile(treeText, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'w':
                            /* Creates a logs on grass tile */
                            newTile = new Maptile(woodText, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'c':
                            /* Creates a cave opening on grass tile */
                            newTile = new Maptile(caveTile, new Vector2(x, y), false, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'r':
                            /* Creates a rock tile */
                            newTile = new Maptile(rockTile, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'v':
                            /* Creates a cave opening on cave tile */
                            newTile = new Maptile(transition, new Vector2(x, y), false, true, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'f':
                            /* Creates a fence tile */
                            newTile = new Maptile(fenceText, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'e':
                            /* Creates a fence tile that closes fence */
                            newTile = new Maptile(fenceEnd, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case 'k':
                            /* Creates a fence tile that closes fence */
                            newTile = new Maptile(waterText, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case '1':
                            /* Creates a part of house tile */
                            newTile = new Maptile(houseText1, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case '2':
                            /* Creates a part of house tile */
                            newTile = new Maptile(houseText2, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case '3':
                            /* Creates a part of house tile */
                            newTile = new Maptile(houseText3, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                        case '4':
                            /* Creates a part of house tile */
                            newTile = new Maptile(houseText4, new Vector2(x, y), true, false, false, false);
                            currentMap[x, y] = newTile;
                            break;
                    }
                }

                }
            }


        public void Draw(SpriteBatch sb)
        {

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    currentMap[x, y].Draw(sb);
                }
            }

        }

        
    }
}