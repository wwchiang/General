using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Collections.Generic;


namespace Project3
{

    public class Enemy
    {
        public Texture2D enemyTexture;

        // Hit points that get docked off every time player hits enemy
        public int HP { set; get; }

        // Enemy speed 
        public int enemySpeed { set; get; }

        // The amount of damage the enemy puts on the player 
        public int Damage { set; get; }

        //Experience that gets added to player's experience after enemy is killed.
        public int Experience { set; get; }

        // Money left behind by killing enemy
        public int bounty;

        //List of items that player can inherit from a dead enemy.
        public List <Item> EnemyItemsList = new List<Item>();

        public string enemyName;


        public Enemy(Texture2D texture, string name, int hp, int speed, int atk, int exp, int money, List<Item> list)
        {
            this.enemyName = name;
            this.enemyTexture = texture;
            HP = hp;
            enemySpeed = speed;
            Damage = atk;
            Experience = exp;
            bounty = money;
            EnemyItemsList = list;
        }

        public Enemy(Enemy e)
        {
            this.enemyName = e.enemyName;
            this.enemyTexture = e.enemyTexture;
            this.HP = e.HP;
            this.enemySpeed = e.enemySpeed;
            this.Damage = e.Damage;
            this.Experience = e.Experience;
            this.bounty = e.bounty;
            this.EnemyItemsList = e.EnemyItemsList;
        }
    }
}
