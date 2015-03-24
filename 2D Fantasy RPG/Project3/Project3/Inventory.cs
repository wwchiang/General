using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Project3
{
    /* For displaying player inventory during gameplay. IE, if the player presses "i", they can 
     change what they have equipped.*/
    public class Inventory
    {
        public List<Item> items;

        public int money;

        public int currentItemIndex;
        public Item currentItem;

        /* These attributes will *only* be used for PLAYER. It will technically
         be possible for a merchant to "equip" items, but it wont have any functionality. */
        public Item equippedShield;
        public Item equippedWeapon;
        public Item equippedPotion;

        public int maxSize = 10;

        public Inventory(List<Item> others)
        {
            this.items = others;
            currentItemIndex = 0;
        }

        public Inventory()
        {
            currentItemIndex = 0;
            items = new List<Item>();
        }

        public Boolean HasItems()
        {
            if (items == null)
            {
                return false;
            }
            return true;
        }
        /* Adds the reference of that item to the player's inventory list. Note that this method AUTOMATICALLY
         rejects the item if the inventory is full.
         
         If the player's inventory is NOT full and we already have a DUPLICATE of that item, then we just add onto
         the quantity. For instance, this makes sense if the player has, say, a HP potion and picks up 3 more. Since
         the player's inventory is already very limited, it would be ridiculous for each potion to have its own slot.*/
        public void AddToInventory(Item item, int quantity)
        {
            if (!InventoryIsFull())
            {
                int itemIndex = 0;
                Boolean exists = false;
                foreach (Item i in items)
                {
                    if (i.itemName.Equals(item.itemName))
                    {
                        exists = true;
                        break;
                    }
                    itemIndex++;
                }

                if (exists)
                {
                    items.ElementAt(itemIndex).AddQuantity(quantity);
                }
                else
                {
                    item.quantity = quantity;
                    items.Add(item);
                }
            }
        }

        //Operates the exact same way for player using a consumable item OR selling it to a shop.
        //Decrements the quantity of that item -- if it hits 0, it automatically gets removed from the inventory.
        public void ConsumeFromInventory(Item item, int quan = 1)   
        {
            int itemIndex = 0;
            foreach (Item i in items)
            {
                if (i.Equals(item))
                {
                    break;
                }
                itemIndex++;
            }

            items.ElementAt(itemIndex).quantity = items.ElementAt(itemIndex).quantity - quan;
            if (items.ElementAt(itemIndex).quantity == 0)
            {
                RemoveFromInventory(item);
            }
        }

        /* Manually removes the reference of that item from the inventory list*/
        public void RemoveFromInventory(Item item)
        {
            items.Remove(item);
        }

        /* Used to iterate through the inventory, mostly used for shop stuff.*/
        #region Browsing Methods
        public void BrowseNextItem()
        {
            currentItemIndex++;
            if (currentItemIndex > items.Count())
            {
                currentItemIndex--;
            }
        }

        public void BrowsePreviousItem()
        {
            currentItemIndex--;
            if (currentItemIndex < 0)
            {
                currentItemIndex++;
            }
        }
        #endregion


        /* *Returns true if the player's inventory is capped to the max, false otherwise */
        public Boolean InventoryIsFull()
        {
            if (items.Count == maxSize)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

    }
}
