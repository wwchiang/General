using Microsoft.Xna.Framework.Graphics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Project3
{
    public class Item
    {
        public Texture2D itemTexture;

        /* Basic INTs for properties - 
         itemBuyPrice - self explanatory
         itemSellPrice - always set to 60% of the buying price
         block - used to calculate how much damage mitigation the player gets when struck in battle. only intended for SHIELDS
         heal - used to calculate how much a potion can heal
         damage - added to player's base damage when they attack*/
        int itemBuyPrice;
        int itemSellPrice;
        public int quantity;
        public int block { get; private set; }
        public int heal { get; private set; }
        public int damage { get; private set; }
        public int weight { get; private set; }

        public Boolean isShield = false;
        public Boolean isPotion = false;
        public Boolean isWeapon = false;
        public String itemName;

        public Item(Texture2D item, String itemName,
            int itemBuyPrice, int ItemGeneralEffect, Boolean isShield, Boolean isPotion, Boolean isWeapon, int weight = 0)
        {
            this.weight = weight;
            itemTexture = item;
            this.itemBuyPrice = itemBuyPrice;
            itemSellPrice = (int)(itemBuyPrice * 0.60);

            if (isShield)
            {
                block = ItemGeneralEffect;
                this.isShield = true;
            }
            else if (isPotion)
            {
                heal = ItemGeneralEffect;
                this.isPotion = true;
            }
            else if (isWeapon)
            {
                damage = ItemGeneralEffect;
                this.isWeapon = true;
            }
            quantity = 1;
            this.itemName = itemName;
        }

        public String GetItemType()
        {
            if (isShield)
            {
                return "shield";
            }
            else if (isPotion)
            {
                return "potion";
            }
            else if (isWeapon)
            {
                return "weapon";
            }
            return "misc";
        }

        public void AddQuantity(int quant)
        {
            this.quantity = this.quantity + quant;
        }

        public void RemoveQuantity()
        {
            this.quantity--;
            if (this.quantity < 0)
            {
                this.quantity = 0;
            }
        }

        public void RemoveQuantity(int quant)
        {
            this.quantity--;
            if (this.quantity < 0)
            {
                this.quantity = 0;
            }
        }

        public int getBuyPrice()
        {
            return itemBuyPrice;
        }

        public int getSellPrice()
        {
            return itemSellPrice;
        }
        public Boolean HasNoneOf()
        {
            if (this.quantity == 0)
            {
                return true;
            }

            return false;
        }
    }
}
