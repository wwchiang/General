using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Project3
{
    public class Shop
    {
        Texture2D money;
        Texture2D selectionmenu;
        Texture2D main;
        Texture2D selectarrow;
        Texture2D shopspeech;

        public World world;

        Inventory playerInventory;
        Inventory shopInventory;
        Boolean isBuying;
        Boolean isSelling;

        Boolean isConfirm;

        Boolean isErrorState;
        String displayText = "Welcome!";
        String displayLine2 = "";
        int quantityOf;

        public Boolean isFinished;
        /* Possible states: 
            0 - Arrow is hovering over BUY
            1 - Arrow is hovering over SELL
            2 - Arrow is hovering over LEAVE*/
        int currentState;

        int currentItemSelect;

        public Shop(Inventory shopInventory)
        {
            this.shopInventory = shopInventory;
            isBuying = false;
            isSelling = false;
            isErrorState = false;
        }

        public void LoadTextures(World world)
        {
            this.world = world;
            money = world.game.Content.Load<Texture2D>("Overlays/speech_money_96x32");

            selectionmenu = world.game.Content.Load<Texture2D>("Overlays/shop_menu_96x96");
            main = world.game.Content.Load<Texture2D>("Overlays/shop_main_224x224");
            selectarrow = world.game.Content.Load<Texture2D>("Overlays/arrow select");
            shopspeech = world.game.Content.Load<Texture2D>("Overlays/shop_speech");
        }

        public void PlayerShop(Inventory playerInventory)
        {
            this.playerInventory = playerInventory;
            currentState = 0;
        }

        public void ResetShop()
        {
            currentItemSelect = 0;
            currentState = 0;
            displayText = "Welcome!";
            isFinished = false;
        }
        #region Update Handler
        public void Update(KeyboardState keyboard)
        {
            // If player is notBuying and notSelling, then they must be in selection
            if (!isBuying && !isSelling)
            {

                if (keyboard.IsKeyDown(Keys.W))
                {
                    currentState--;
                    if (currentState < 0)
                    {
                        currentState = 0;
                    }
                }
                else if (keyboard.IsKeyDown(Keys.S))
                {
                    currentState++;
                    if (currentState > 2)
                    {
                        currentState = 2;
                    }
                }
            }

            //If the player hits enter during selection and it is NOT selling or buying
            if (keyboard.IsKeyDown(Keys.Enter) && !isSelling && !isBuying)
            {
                if (currentState == 2)
                {
                    isFinished = true;
                    displayText = "See you later!";
                    //Quit the shop, probably by calling it from player. 
                }
                else if (currentState == 1)
                {
                    //Enter SELL MODE
                    isSelling = true;
                    isBuying = false;
                    return;
                }
                else if (currentState == 0)
                {
                    //Enter BUY MODE
                    isBuying = true;
                    isSelling = false;
                    return;
                }
            }

            //If the player hits back during buy/sell and it is not a confirm state nor error, exit out of it.
            if (keyboard.IsKeyDown(Keys.Back) && !isConfirm && !isErrorState)
            {
                if (isSelling)
                {
                    isSelling = false;
                }
                else if (isBuying)
                {
                    isBuying = false;
                }
                currentItemSelect = 0;
                quantityOf = 0;
                displayText = "How can I help you?";
                displayLine2 = "";
            }

            if (isBuying)
            {
                HandlePurchases(keyboard);
            }

            else if (isSelling)
            {
                HandleSales(keyboard);
            }
        }
        #endregion

        #region Buying Items Region
        //If player is in buying mode, if the player presses W, move up, and presses S, move down. 
        //if press enter, inquire how much to buy, and if press enter again, calculates if player has
        //enough money to buy. if so, player gets x quantity of that item added to inventory? 
        public void HandlePurchases(KeyboardState kb)
        {
            displayLine2 = "";
            if (!isErrorState)
            {
                if (kb.IsKeyDown(Keys.W) && !isConfirm)
                {
                    currentItemSelect--;
                    if (currentItemSelect < 0)
                    {
                        currentItemSelect = 0;
                    }
                }
                if (kb.IsKeyDown(Keys.S) && !isConfirm)
                {
                    currentItemSelect++;
                    if (currentItemSelect > shopInventory.items.Count() - 1)
                    {
                        currentItemSelect = shopInventory.items.Count - 1;
                    }
                }

                if (kb.IsKeyDown(Keys.Enter) && !isConfirm)
                {
                    isConfirm = true;
                    quantityOf = 1;
                    Item itemBuy = shopInventory.items.ElementAt(currentItemSelect);
                    displayText = itemBuy.itemName + " x " + quantityOf + "?";
                    displayLine2 = "That'll be " + itemBuy.getBuyPrice();
                    return;
                }

                if (isConfirm == true)
                {
                    if (kb.IsKeyDown(Keys.W) && kb.IsKeyUp(Keys.S))
                    {
                        quantityOf++;

                    }
                    else if (kb.IsKeyDown(Keys.S) && kb.IsKeyUp(Keys.W))
                    {
                        quantityOf--;
                        if (quantityOf < 1)
                        {
                            quantityOf = 1;
                        }
                    }
                    Item itemBuy = shopInventory.items.ElementAt(currentItemSelect);
                    displayText = itemBuy.itemName + " x " + quantityOf + "?";
                    int totalPrice = itemBuy.getBuyPrice() * quantityOf;
                    displayLine2 = "That'll be " + totalPrice;
                    if (kb.IsKeyDown(Keys.Back))
                    {
                        isConfirm = false;
                        displayText = "Anything else?";
                        displayLine2 = "";
                    }
                    else if (kb.IsKeyDown(Keys.Enter))
                    {
                        //displayText = itemBuy.itemName + 
                        int endAmount = playerInventory.money - totalPrice;
                        if (endAmount < 0)
                        {
                            isErrorState = true;
                            displayText = "You need more cash!";
                            displayLine2 = "";
                            //Set display message to not enough money error

                        }
                        else if (endAmount > 0 && playerInventory.InventoryIsFull())
                        {
                            isErrorState = true;
                            displayText = "Your bags are full!";
                            displayLine2 = "";
                            //Set display message to inventory full error

                        }
                        else
                        {
                            playerInventory.money = playerInventory.money - totalPrice;
                            playerInventory.AddToInventory(itemBuy, quantityOf);
                        }
                    }
                }
            }
            //Otherwise, it is in the error state, and the only thing the player can do is press enter to go back 
            else
            {
                if (kb.IsKeyDown(Keys.Enter))
                {
                    displayText = "Anything else?";
                    isErrorState = false;
                    isConfirm = false;
                    quantityOf = 0;
                }
            }
        }
        #endregion

        #region Selling Items Region
        public void HandleSales(KeyboardState kb)
        {
            if (kb.IsKeyDown(Keys.W) && !isConfirm)
            {
                currentItemSelect--;
                if (currentItemSelect < 0)
                {
                    currentItemSelect = 0;
                }
            }
            if (kb.IsKeyDown(Keys.S) && !isConfirm)
            {
                currentItemSelect++;
                if (currentItemSelect > playerInventory.items.Count() - 1)
                {
                    currentItemSelect = playerInventory.items.Count - 1;
                }
            }

            Item itemSell = playerInventory.items.ElementAt(currentItemSelect);
            if (kb.IsKeyDown(Keys.Enter) && !isConfirm)
            {
                displayText = "I'll pay you " + itemSell.getSellPrice();
                displayLine2 = "for a " + itemSell.itemName + ".";
                isConfirm = true;
                quantityOf = 1;
                return;
            }

            if (isConfirm == true)
            {
                //if (kb.IsKeyDown(Keys.W))
                //{
                //    quantityOf++;
                //    if (quantityOf > itemSell.quantity)
                //    {
                //        quantityOf = itemSell.quantity;
                //    }
                //}
                //if (kb.IsKeyDown(Keys.S))
                //{
                //    quantityOf--;
                //    if (quantityOf < 1)
                //    {
                //        quantityOf = 1;
                //    }
                //}
                if (kb.IsKeyDown(Keys.Back))
                {
                    isConfirm = false;
                }
                if (kb.IsKeyDown(Keys.Enter))
                {
                    if (playerInventory.items.Count == 1)
                    {
                        displayText = "You can't have an ";
                        displayLine2 = "empty inventory!";
                        isConfirm = false;
                        return;
                    }
                    //Selling logic -- since we handle item limitations, we can freely sell via integer quantity.
                    int sellPrice = itemSell.getSellPrice();
                    playerInventory.money = playerInventory.money + sellPrice;

                    if (itemSell.quantity == 1)
                    {
                        if (currentItemSelect != 0)
                        {
                            currentItemSelect--;
                        }
                    }
                    playerInventory.ConsumeFromInventory(itemSell, 1);
                    displayText = "Thank you!";
                    displayLine2 = "";
                    isConfirm = false;
                    /* if */
                }

            }
        }
        #endregion

        public void Draw(SpriteBatch sb)
        {
            //Speech Bubble - Left Side
            Vector2 speech_pos = new Vector2(world.camera.Position.X / 2, world.camera.Position.Y / 2 + 32);
            sb.Draw(shopspeech, speech_pos, Color.White);

            //Money Display - Top Left
            Vector2 money_pos = new Vector2(world.camera.Position.X / 2, world.camera.Position.Y / 2);
            sb.Draw(money, money_pos, Color.White);

            sb.DrawString(world.font, playerInventory.money.ToString(), money_pos + new Vector2(8, 6), Color.White);
            if (!isBuying && !isSelling)
            {
                //Display speech default
                sb.DrawString(world.shopDialogueFont, displayText, speech_pos + new Vector2(10, 8), Color.White);

                //Start menu selection
                Vector2 menu_pos = new Vector2(world.camera.Position.X / 2 + 384, world.camera.Position.Y / 2);
                Vector2 arrow_pos = menu_pos + new Vector2(8, 10 + currentState * 32);
                sb.Draw(selectionmenu, menu_pos, Color.White);
                sb.Draw(selectarrow, arrow_pos, Color.White);
                sb.DrawString(world.font, "Buy", menu_pos + new Vector2(25, 10 + 0 * 32), Color.White);
                sb.DrawString(world.font, "Sell", menu_pos + new Vector2(25, 7 + 1 * 32), Color.White);
                sb.DrawString(world.font, "Leave", menu_pos + new Vector2(25, 5 + 2 * 32), Color.White);
            }

            else if (isBuying || isSelling)
            {
                Vector2 main_pos = new Vector2(world.camera.Position.X / 2 + 256, world.camera.Position.Y / 2);
                sb.Draw(main, main_pos, Color.White);

                if (isBuying)
                {
                    int[] pages = { 5, 10, 15, 20, 25, 30 };
                    int currentpage = 1;
                    Vector2 item_name_pos = main_pos + new Vector2(25, 10);
                    for (int i = 0; i < 5; i++)
                    {
                        int addToPage = i;
                        if (currentItemSelect >= 5 && currentItemSelect < 10)
                        {
                            currentpage = 2;
                            addToPage += 5;
                        }
                        if (currentItemSelect >= 10 && currentItemSelect < 15)
                        {
                            currentpage = 3;
                            addToPage += 10;
                        }
                        if (currentItemSelect >= 15 && currentItemSelect < 20)
                        {
                            currentpage = 4;
                            addToPage += 15;
                        }
                        if (currentItemSelect >= 20 && currentItemSelect < 25)
                        {
                            currentpage = 5;
                            addToPage += 20;
                        }
                        if (currentItemSelect >= 25 && currentItemSelect < 30)
                        {
                            currentpage = 6;
                            addToPage += 25;
                        }
                        sb.Draw(shopInventory.items.ElementAt(addToPage).itemTexture, item_name_pos + new Vector2(-5, 32 * i - 4), Color.White);
                        sb.DrawString(world.shopDialogueFont, shopInventory.items.ElementAt(addToPage).itemName, item_name_pos + new Vector2(27, 32 * i), Color.White);
                        
                    }
                    sb.DrawString(world.font, "Page " + currentpage + "/6", main_pos + new Vector2(64, 192), Color.White);
                    //if is buying, display first 5 items. if it goes past, "scroll" onto the next 5... etc
                    //display brackets -- if currentItemSelect + 1 % 
                    int offset = currentItemSelect % 5;
                    Vector2 selectArrowPos = main_pos + new Vector2(10, 10 + 32 * offset);
                    sb.Draw(selectarrow, selectArrowPos, Color.White);

                    //Display npc dialogue on side
                    sb.DrawString(world.shopDialogueFont, displayText, speech_pos + new Vector2(10, 8), Color.White);
                    sb.DrawString(world.shopDialogueFont, displayLine2, speech_pos + new Vector2(10, 24), Color.White);
                }
                else if (isSelling)
                {
                    int[] pages = { 5, 10 };
                    int currentpage = 1;
                    Vector2 item_name_pos = main_pos + new Vector2(25, 10);
                    for (int i = 0; i < 5; i++)
                    {
                        int addToPage = i;
                        if (currentItemSelect >= 5 && currentItemSelect < 10)
                        {
                            currentpage = 2;
                            addToPage += 5;
                        }
                        if (addToPage < playerInventory.items.Count)
                        {
                            sb.Draw(playerInventory.items.ElementAt(addToPage).itemTexture, item_name_pos + new Vector2(-5, 32 * i - 4), Color.White);
                            sb.DrawString(world.shopDialogueFont, playerInventory.items.ElementAt(addToPage).itemName, item_name_pos + new Vector2(27, 32 * i), Color.White);

                        }

                    }
                    sb.DrawString(world.font, "Page " + currentpage + "/6", main_pos + new Vector2(64, 192), Color.White);
                    //if is buying, display first 5 items. if it goes past, "scroll" onto the next 5... etc
                    //display brackets -- if currentItemSelect + 1 % 
                    int offset = currentItemSelect % 5;
                    Vector2 selectArrowPos = main_pos + new Vector2(10, 10 + 32 * offset);
                    sb.Draw(selectarrow, selectArrowPos, Color.White);

                    //Display npc dialogue on side
                    sb.DrawString(world.shopDialogueFont, displayText, speech_pos + new Vector2(10, 8), Color.White);
                    sb.DrawString(world.shopDialogueFont, displayLine2, speech_pos + new Vector2(10, 24), Color.White);
                }
                // First want to draw the box underlays based on states. 
                // If the player ISNT buying OR selling, then it is Buy/Sell/Leave draw.

                // If it is buying OR selling, we draw the item selections, money
                // Then the position of the selection arrow
            }
        }
    }
}
