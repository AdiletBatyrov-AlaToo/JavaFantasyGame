import java.util.Random;
import java.util.Scanner;

class Game {
    private Player player;
    private Shop shop;
    private boolean isGameActive;

    public Game() {
        player = new Player();
        shop = new Shop(player);
        isGameActive = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (isGameActive) {
            player.displayStatus();
            System.out.println("Выберите одно из следующих действий: " +
                    "1) исследовать лес, " +
                    "2) отхилиться (+25хп), " +
                    "3) отправиться в горы, " +
                    "4) отправиться в пещеру, " +
                    "5) посетить магазин");
            int choice = scanner.nextInt();
            handleChoice(choice);
        }
        scanner.close();
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1:
                exploreForest();
                break;
            case 2:
                player.heal();
                break;
            case 3:
                exploreMountains();
                break;
            case 4:
                exploreCave();
                break;
            case 5:
                shop.visit();
                break;
            default:
                System.out.println("Вы ввели неверную команду");
                break;
        }
    }

    private void exploreForest() {
        Enemy enemy = new Enemy("Орк", 50, 8);
        System.out.println("Вы отправились в лес и встретили " + enemy.getName() + " с " + enemy.getHealth() + " здоровья");
        battle(enemy);
    }

    private void exploreMountains() {
        Random rand = new Random();
        Enemy enemy = rand.nextBoolean() ? new Enemy("Гоблин", 30, 5) : new Enemy("Тролль", 60, 10);
        System.out.println("Вы отправились в горы и встретили " + enemy.getName() + " с " + enemy.getHealth() + " здоровья");
        battle(enemy);
    }

    private void exploreCave() {
        Random rand = new Random();
        Enemy enemy = rand.nextBoolean() ? new Enemy("Скелет", 40, 6) : new Enemy("Призрак", 35, 7);
        System.out.println("Вы отправились в пещеру и встретили " + enemy.getName() + " с " + enemy.getHealth() + " здоровья");
        battle(enemy);
    }

    private void battle(Enemy enemy) {
        Scanner scanner = new Scanner(System.in);
        while (enemy.isAlive() && player.isAlive()) {
            System.out.println("1 - Атаковать, 2 - Использовать способность (двойной урон)");
            int choice = scanner.nextInt();
            if (choice == 1) {
                player.attack(enemy);
            } else if (choice == 2) {
                player.useAbility(enemy);
            } else {
                System.out.println("Неверный выбор, попробуйте еще раз.");
                continue; // Повторить текущий цикл
            }
            if (enemy.isAlive()) {
                enemy.attack(player);
            }
        }
        if (!player.isAlive()) {
            isGameActive = false;
            System.out.println("Вы проиграли");
        } else {
            System.out.println("Вы победили " + enemy.getName() + "!");
            player.gainExperience(50);
            player.addGold(20); // Добавляем золото за победу
            System.out.println("Вы получили 20 золота!");
            if (player.levelUp()) {
                System.out.println("Поздравляем! Ваш уровень повышен до " + player.getLevel() + ".");
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}

class Player {
    private int health;
    private int damage;
    private int experience;
    private int level;
    private int gold;

    public Player() {
        health = 100;
        damage = 10;
        experience = 0;
        level = 1;
        gold = 0;
    }

    public void displayStatus() {
        System.out.println("Ваш персонаж имеет: " + health + " здоровья");
        System.out.println("Ваш опыт: " + experience + " / 100");
        System.out.println("Ваш уровень: " + level);
        System.out.println("Ваш урон: " + damage);
        System.out.println("Ваше золото: " + gold);
    }

    public void attack(Enemy enemy) {
        System.out.println("Вы атакуете " + enemy.getName() + " и наносите " + damage + " урона!");
        enemy.takeDamage(damage);
    }

    public void useAbility(Enemy enemy) {
        int abilityDamage = damage * 2;
        System.out.println("Вы используете способность и наносите " + abilityDamage + " урона!");
        enemy.takeDamage(abilityDamage);
    }

    public void heal() {
        health += 25;
        if (health > 100) health = 100;
        System.out.println("Вы отдохнули, ваше здоровье: " + health);
    }

    public void gainExperience(int amount) {
        experience += amount;
        System.out.println("Вы получили " + amount + " опыта");
    }

    public boolean levelUp() {
        if (experience >= 100) {
            level++;
            damage += 10;
            health = 100;
            experience -= 100;
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getLevel() {
        return level;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public int getGold() {
        return gold;
    }

    public void buyItem(String item, int cost) {
        if (gold >= cost) {
            gold -= cost;
            System.out.println("Вы купили " + item + "!");
        } else {
            System.out.println("Недостаточно золота для покупки " + item + ".");
        }
    }
}

class Enemy {
    private String name;
    private int health;
    private int damage;

    public Enemy(String name, int health, int damage) {
        this.name = name;
        this.health = health;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void attack(Player player) {
        System.out.println(name + " атакует вас и наносит " + damage + " урона");
        player.takeDamage(damage);
    }
}

class Shop {
    private Player player;

    public Shop(Player player) {
        this.player = player;
    }

    public void visit() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Добро пожаловать в магазин!");
            System.out.println("Ваше золото: " + player.getGold());
            System.out.println("Доступные предметы:");
            System.out.println("1) Меч - 50 золота");
            System.out.println("2) Щит - 30 золота");
            System.out.println("3) Выход из магазина");
            System.out.print("Выберите предмет для покупки: ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                player.buyItem("Меч", 50);
            } else if (choice == 2) {
                player.buyItem("Щит", 30);
            } else if (choice == 3) {
                System.out.println("Вы вышли из магазина.");
                break;
            } else {
                System.out.println("Неверный выбор, попробуйте еще раз.");
            }
        }
    }
}
