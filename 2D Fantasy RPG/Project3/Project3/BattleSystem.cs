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

namespace Project3
{
    public class BattleSystem
    {

        Display HUD;
        Player player;
        World world;
        int playerHealth;

        bool endOfBattle;
        bool win;
        bool lose;
        Boolean canFight;
        public Boolean inEnemySelect;
        public Boolean inChoices;

        int expGained;
        int levelGained;
        int moneyGained;
        string typeBG;
        int displayCounter;

        List<Enemy> enemyList;
        List<Item> itemGained;

        List<string> combatHistory;

        public Boolean exitBattle;
        public int currentSelect;
        public int enemySelect;

        Texture2D field;
        Texture2D forest;
        Texture2D cave;
        Texture2D playersprite;
        Texture2D combattext;
        Texture2D options;
        Texture2D playerdisplay;
        Texture2D target;
        Texture2D enemydisplay;
        Texture2D arrow;

        Random r;

        public BattleSystem(Player player, Display display, World world)
        {
            this.world = world;
            this.player = player;
            this.HUD = display;

            expGained = 0;
            levelGained = 0;
            moneyGained = 0;
            itemGained = new List<Item>();
            enemyList = new List<Enemy>();
            combatHistory = new List<string>();
            playerHealth = display.HP;
            endOfBattle = false;
            win = false;
            lose = false;
            displayCounter = 0;
            exitBattle = false;
            inChoices = false;
            typeBG = "";
            r = new Random();
        }

        public void LoadTextures()
        {
            Game1 g = world.game;
            arrow = g.Content.Load<Texture2D>("Overlays/arrow select");
            field = g.Content.Load<Texture2D>("Battle/plainsbg");
            forest = g.Content.Load<Texture2D>("Battle/forest");
            cave = g.Content.Load<Texture2D>("Battle/cavebg");
            playersprite = g.Content.Load<Texture2D>("Battle/playerbattlesprite");
            combattext =  g.Content.Load<Texture2D>("Battle/combattext");
            options = g.Content.Load<Texture2D>("Battle/options");
            playerdisplay = g.Content.Load<Texture2D>("Battle/playerdisplay");
            target = g.Content.Load<Texture2D>("Battle/target");
            enemydisplay = g.Content.Load<Texture2D>("Battle/enemydisplay");
        }

        public void GenerateBattle(List<Enemy> enemies, String type)
        {
            typeBG = type;
            playerHealth = HUD.HP;
            Random r = new Random();
            int num = r.Next(enemies.Count);
            Enemy e = new Enemy(enemies.ElementAt(num));
            enemyList.Add(e);

            combatHistory.Add("A " + e.enemyName + " appeared!");

            if (IsSuccessfulSmaller())
            {
                num = r.Next(enemies.Count);
                e = new Enemy(enemies.ElementAt(num));
                enemyList.Add(e);
                combatHistory.Add("A " + e.enemyName + " appeared!");
            }

            if (IsSuccessfulSmaller())
            {
                num = r.Next(enemies.Count);
                e = new Enemy(enemies.ElementAt(num));
                enemyList.Add(e);
                combatHistory.Add("A " + e.enemyName + " appeared!");
            }

            inChoices = false;
        }

        // Update method 
        public void Update(KeyboardState keyboard)
        {
            //If 'W' is down, we want to move the selection arrow up. IE, from "HEAL" to "FIGHT".
            if (keyboard.IsKeyDown(Keys.W) && !inEnemySelect && !exitBattle && inChoices)
            {
                currentSelect--;
                if (currentSelect < 0)
                {
                    currentSelect = 0;
                }
                return;
            }

            //Otherwise, if 'S' is down, we want to move the selection arrow down. IE, from "HEAL" to "RUN"
            else if (keyboard.IsKeyDown(Keys.S) && !inEnemySelect && !exitBattle && inChoices)
            {
                currentSelect++;
                if (currentSelect > 2)
                {
                    currentSelect = 2;
                }
                return;
            }

            //Else, if ENTER is down, we want to engage in whatever option was chosen.
            else if (keyboard.IsKeyDown(Keys.Enter) && !inEnemySelect && !exitBattle && inChoices)
            {
                
                //If it's 0, we want to fight. Enter enemy selection state.
                if (currentSelect == 0)
                {
                    
                    inEnemySelect = true;
                    inChoices = false;
                    
                }
                //If it's 1, we want to heal. Consume current healing item.
                if (currentSelect == 1)
                {
                    UseConsumable();
                    inChoices = false;
                }
                //If it's 2, we want to run. Try running.
                if (currentSelect == 2)
                {
                    Escape();
                    inChoices = false;
                }
            }

            //If we are in enemy selection, delegate control to SelectEnemies()
            else if (inEnemySelect)
            {
                SelectEnemies(keyboard);
            }

            //Otherwise, if not in any of the above states, we must have computed battle 
            else
            {
                
                //Assumes that combat calculation has been done
                if (keyboard.IsKeyDown(Keys.Enter) && !endOfBattle)
                {
                    
                    if (displayCounter < combatHistory.Count)
                    {
                        
                        displayCounter++;
                    }
                    else
                    {
                        combatHistory.Clear();
                        combatHistory.Add("What will you do?");
                        displayCounter = 1;
                        inChoices = true;
                    }
                    
                    
                }
                else if (keyboard.IsKeyDown(Keys.Enter) && endOfBattle)
                {

                    if (displayCounter < combatHistory.Count)
                    {
                        displayCounter++;
                    }
                    else
                    {

                        transition(keyboard);
                    }

                }

                //On every update, we check if the whole battle is over. If not, we continue as usual. 
                //Note that you can only get endOfBattle to TRUE under three conditions:
                //1. Kill all monsters
                //2. Player dies
                //3. Player runs
                
            }

        }

        // Random generator. 
        private Boolean IsSuccessful()
        {
            int num = r.Next(10);

            if (num < 7)
                return true;
            else
                return false;
        }

        private Boolean IsSuccessfulSmaller()
        {
            int num = r.Next(10);

            if (num < 3)
                return true;
            else
                return false;
        }

        private void SelectEnemies(KeyboardState kb)
        {
            if (kb.IsKeyDown(Keys.Back))
            {
                enemySelect = 0;
                inEnemySelect = false;
                inChoices = true;
                return;
            }

            if (kb.IsKeyDown(Keys.W))
            {
                
                enemySelect++;
                if (enemySelect > enemyList.Count - 1)
                {
                    enemySelect = enemyList.Count - 1;
                }
            }

            if (kb.IsKeyDown(Keys.S))
            {
                enemySelect--;

                if (enemySelect < 0)
                {
                    enemySelect = 0;
                }
            }

            if (kb.IsKeyDown(Keys.Enter))
            {
                inEnemySelect = false;
                canFight = true;
                combatHistory.Clear();
                displayCounter = 0;
                Battle();
            }
        }
        private void UseConsumable()
        {
            if (player.consumable != null && !player.consumable.itemName.Equals("None"))
            {
                combatHistory.Clear();
                playerHealth += player.consumable.heal;
                
                if (playerHealth > HUD.HP)
                {
                    playerHealth = HUD.HP;
                }
                
                combatHistory.Add("Used " + player.consumable.itemName + " and healed " + player.consumable.heal); 
                player.playerInventory.RemoveFromInventory(player.consumable);
                player.consumable = null;
                canFight = false;
                Battle();
            }
            else
            {
                combatHistory.Clear();
                combatHistory.Add("You're out of healing items!");
                displayCounter = 1;
            }
        }

        private void Escape()
        {
            if (IsSuccessful())
            {
                //Sets endOfBattle to true, player neither lost or won. 
                combatHistory.Clear();
                combatHistory.Add("You successfully escaped!");
                endOfBattle = true;
                lose = false;
                win = false;
            }
            else
            {
                combatHistory.Clear();
                combatHistory.Add("You failed to escape!");
                canFight = false;
                Battle();
            }
        }

        /*
         * Method that actually subtracts the health and whatnot 
         */
        private void Battle()
        {

            //world.attackSound.Play();
            for (int i = 0; i < enemyList.Count; i++)
            {
                Enemy e = enemyList.ElementAt(i);
                if (player.speed > e.enemySpeed && i == enemySelect)
                {
                    //Faster player trying to hit. 
                    if (canFight)
                    {
                        if (IsSuccessful())
                        {
                            combatHistory.Add("Hit " + e.enemyName + " for " + player.atk + ".");
                            e.HP = e.HP - player.atk;
                        }
                        else
                        {
                            combatHistory.Add("Missed the " + e.enemyName + "!");
                        }
                    }
                    //Slower Enemy Trying to hit
                    if (e.HP > 0)
                    {
                        if (IsSuccessful())
                        {
                            int damage = e.Damage - player.def;
                            if (damage < 0)
                            {
                                damage = 0;
                            }
                            combatHistory.Add(e.enemyName + " hit you for " + damage + ".");
                            playerHealth = playerHealth - damage;
                        }
                        else
                        {
                            combatHistory.Add(e.enemyName + " missed!");
                        }
                    }
                }
                else if (player.speed < e.enemySpeed && i == enemySelect) 
                {
                    //Faster Enemy hitting
                    if (IsSuccessful())
                    {
                        int damage = e.Damage - player.def;
                        if (damage < 0)
                        {
                            damage = 0;
                        }
                        combatHistory.Add(e.enemyName + " hit you for " + damage + ".");
                        playerHealth = playerHealth - damage;
                    }
                    else
                    {
                        combatHistory.Add(e.enemyName + " missed!");
                    }
                    //Slower player hitting
                    if (playerHealth > 0) 
                    {
                        if (canFight)
                        {
                            if (IsSuccessful())
                            {
                                combatHistory.Add("Hit " + e.enemyName + " for " + player.atk + ".");
                                e.HP = e.HP - player.atk;
                            }
                            else
                            {
                                combatHistory.Add("Missed the " + e.enemyName + "!");
                            }
                        }
                    }
                }
                    
                else
                {
                    if (IsSuccessful())
                    {
                        int damage = e.Damage - player.def;
                        if (damage < 0)
                        {
                            damage = 0;
                        }
                        combatHistory.Add(e.enemyName + " hit you for " + damage + ".");
                        playerHealth = playerHealth - damage;
                    }
                    else
                    {
                        combatHistory.Add(e.enemyName + " missed!");
                    }
                }
            }
            checkOutcome();
        }

        private void checkOutcome()
        {
            enemySelect = 0;
            if (playerHealth <= 0)
            {
                lose = true;
                endOfBattle = true;
                combatHistory.Clear();
                combatHistory.Add("You were defeated in battle.");
                int lost = (int)(player.playerInventory.money * 0.2);
                combatHistory.Add("You lost $" + lost);
                player.playerInventory.money -= lost;
            }
            else
            {
                List<Enemy> toRemove = new List<Enemy>();
                foreach (Enemy e in enemyList)
                {
                    if (e.HP <= 0)
                    {
                        displayCounter = 1;
                        combatHistory.Add(e.enemyName + " was defeated!");

                        expGained += e.Experience;
                        moneyGained += e.bounty;
                        gainItemSpoils();
                        toRemove.Add(e);
                    }
                }
                foreach(Enemy e in toRemove) 
                {
                    enemyList.Remove(e);
                }
                if (enemyList.Count == 0)
                {
                    displayCounter = 1;
                    win = true;
                    endOfBattle = true;

                    int total = expGained + HUD.experience;

                    // Checks if it has reached above threshold 
                    if (total > HUD.threshold)
                    {
                        // Increases level 
                        combatHistory.Add("You gained a level!");
                        HUD.increaseLevel();
                        levelGained = HUD.level;

                        // Increases threshold by 50
                        HUD.increaseThreshold();
                    }

                    // Sets experience 
                    int remainder = total - HUD.threshold;
                    HUD.setExperience(remainder);

                    // Increases player money 
                    player.playerInventory.money += moneyGained;
                    combatHistory.Add("You won and got $" + moneyGained + ".");

                    foreach (Item i in itemGained)
                    {
                        if (player.playerInventory.items.Count < player.playerInventory.maxSize)
                        {
                            combatHistory.Add("Found a " + i.itemName + ".");
                            player.playerInventory.AddToInventory(i, 1);
                        }
                        else
                        {
                            combatHistory.Add("Your bags are full!");
                            break;
                        }
                    }

                }
            }
        }

        // parameter is either enemy or enemy list
        // if list, see below if statement 
        private void checkBattleWin(Enemy e)
        {
            if (e.HP <= 0)
            {
                expGained += e.Experience;
                moneyGained += e.bounty;
                gainItemSpoils();
                enemyList.Remove(e);
            }

            if (enemyList.Count == 0)
            {
                win = true;
                endOfBattle = true;

                int total = expGained + HUD.experience;

                // Checks if it has reached above threshold 
                if (total > HUD.threshold)
                {
                    // Increases level 
                    HUD.increaseLevel();
                    levelGained = HUD.level;

                    // Increases threshold by 50
                    HUD.increaseThreshold();
                }

                // Sets experience 
                int remainder = total - HUD.threshold;
                HUD.setExperience(remainder);

                // Increases player money 
                player.playerInventory.money += moneyGained;
            }

        }
        private void checkBattleLose(int health)
        {
            if (playerHealth <= 0)
            {
                lose = true;
                endOfBattle = true;
            }
        }

        private void gainItemSpoils()
        {
            int num = r.Next(10);

            if (num < 4)
            {
                int randomize = r.Next(enemyList[0].EnemyItemsList.Count);
                itemGained.Add(enemyList[0].EnemyItemsList[randomize]);
            }
        }


        private void transition(KeyboardState keyboard)
        {

            if (!lose && !win)
            {
                // only reaches here if player decides to escape and is successful
                expGained = 0;
                levelGained = 0;
                moneyGained = 0;
            }

            if (keyboard.IsKeyDown(Keys.Enter))
            {
                //world.menuSound.Play();
                exitBattle = true;
                //Now we can exit.
            }
        }

        public void ResetBattle()
        {
            enemyList.Clear();

            currentSelect = 0;
            enemySelect = 0;
            exitBattle = false;
            displayCounter = 0;
            combatHistory.Clear();
            endOfBattle = false;
            canFight = true;
            win = false;
            lose = false;

            expGained = 0;
            levelGained = 0;
            moneyGained = 0;
            itemGained.Clear();
        }

        public void Draw(SpriteBatch sb)
        {
            Vector2 origin = new Vector2(world.camera.Position.X / 2, world.camera.Position.Y / 2);

            if (typeBG.Equals("field"))
            {
                sb.Draw(field, origin, Color.White);
            }
            if (typeBG.Equals("forest"))
            {
                sb.Draw(forest, origin, Color.White);
            }
            if (typeBG.Equals("cave"))
            {
                sb.Draw(cave, origin, Color.White);
            }
            //Combat History
            Vector2 combatpos = origin;
            sb.Draw(combattext, combatpos, Color.White);
            //Options
            Vector2 optionpos = origin + new Vector2(416, 0);
            sb.Draw(options, optionpos, Color.White);
            //Player Sprite
            Vector2 playerpos = origin + new Vector2(112, 224);
            sb.Draw(playersprite, playerpos, Color.White);

            #region Options Draw
            Vector2 optionTextPos = optionpos + new Vector2(18, 16);
            sb.DrawString(world.battleFont, "FIGHT", optionTextPos + new Vector2(0, 0), Color.White);
            sb.DrawString(world.battleFont, "HEAL", optionTextPos + new Vector2(0, 16), Color.White);
            sb.DrawString(world.battleFont, "RUN", optionTextPos + new Vector2(0, 32), Color.White);
            Vector2 optionSelPos = optionTextPos + new Vector2(-12, currentSelect * 16);
            sb.Draw(arrow, optionSelPos, Color.White);
            #endregion
            #region Enemy Draw
            Vector2 enemy1 = new Vector2();
            Vector2 enemy2 = new Vector2();
            Vector2 enemy3 = new Vector2();
            //Drawing Enemies
            if (enemyList.Count >= 1)
            {
                enemy1 = origin + new Vector2(256, 240);
                sb.Draw(enemyList.ElementAt(0).enemyTexture, enemy1, Color.White);
            }
            if (enemyList.Count >= 2)
            {
                enemy2 = origin + new Vector2(320, 256);
                sb.Draw(enemyList.ElementAt(1).enemyTexture, enemy2, Color.White);
            }
            if (enemyList.Count >= 3)
            {
                enemy3 = origin + new Vector2(384, 240);
                sb.Draw(enemyList.ElementAt(2).enemyTexture, enemy3, Color.White);
            }
            #endregion

            #region Combat Text Draw
            Vector2 combatTextPos = origin + new Vector2(12, 8);
            for (int i = 0; i < displayCounter; i++)
            {
                Vector2 textPos = combatTextPos + new Vector2(0, i*16);
                sb.DrawString(world.shopDialogueFont, combatHistory.ElementAt(i), textPos, Color.White);
            }
            #endregion

            #region Player Properties Draw
            //Player Display
            Vector2 playerdisplaypos = origin + new Vector2(0, 128);
            sb.Draw(playerdisplay, playerdisplaypos, Color.White);
            Vector2 playerPropPos = playerdisplaypos + new Vector2(16, 16);

            sb.DrawString(world.battleFont, "HP: " + playerHealth + "/" + HUD.HP, playerPropPos + new Vector2(0, 0), Color.White);
            sb.DrawString(world.battleFont, "ATK: " + player.atk, playerPropPos + new Vector2(0, 16), Color.White);
            sb.DrawString(world.battleFont, "DEF: " + player.def, playerPropPos + new Vector2(0, 32), Color.White);
            #endregion

            if (inEnemySelect)
            {
                //Enemy Name
                Vector2 enemynamepos = origin + new Vector2(64, 320);
                Vector2 enemydisplaypos = enemynamepos + new Vector2(192, 0);
                sb.Draw(enemydisplay, enemynamepos, Color.White);
                sb.Draw(enemydisplay, enemydisplaypos, Color.White);

                Enemy e = enemyList.ElementAt(enemySelect);
                int offset = (e.enemyName.Length * 8) / 2;
                sb.DrawString(world.battleFont, e.enemyName, enemynamepos + new Vector2(80 - offset, 8), Color.White);
                sb.DrawString(world.battleFont, "HP: " + e.HP + "      ATK: " + e.Damage, enemydisplaypos + new Vector2(16, 8), Color.White);

                Vector2 targetPos = new Vector2();
                if (enemySelect == 0)
                {
                    targetPos = enemy1 + new Vector2(16, 16);
                }
                if (enemySelect == 1)
                {
                    targetPos = enemy2 + new Vector2(16, 16);
                }
                if (enemySelect == 2)
                {
                    targetPos = enemy3 + new Vector2(16, 16);
                }
                sb.Draw(target, targetPos, Color.White);
            }
        }
    }
}
