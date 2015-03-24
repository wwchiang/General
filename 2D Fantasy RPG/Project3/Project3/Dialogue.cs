using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Project3
{
    public class Dialogue
    {
        public World world;
        public List<String> text;
        public int currentLine;
        public int[] dialoguePoints;
       

        public Dialogue(World world)
        {
            this.world = world;        }

        public Dialogue(List<String> text)
        {
            this.text = text;
        }

        public void SetDialogue(List<String> lines)
        {
            this.text = lines;
        }
        public void AdvanceLine()
        {
            currentLine++;
            if (currentLine == text.Count)
            {
                currentLine = text.Count - 1;
            }
        }

        public void ResetDialogue()
        {
            currentLine = 0;
        }

        public void AddLine(String line)
        {
            text.Add(line);
        }

        public void Update(KeyboardState keyboard)
        {
            if (!isFinished())
                {
                    AdvanceLine();
                }
        }

        public Boolean isFinished()
        {
            if (currentLine >= text.Count - 1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        public void Draw(SpriteBatch sb)
        {
            Vector2 position = new Vector2(world.camera.Position.X / 2 + 45, world.camera.Position.Y / 2 + 8);

            sb.DrawString(world.font, text.ElementAt(currentLine), position, Color.White);
        }

    }
}
