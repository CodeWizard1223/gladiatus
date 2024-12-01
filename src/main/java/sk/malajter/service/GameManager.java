package sk.malajter.service;

import sk.malajter.ability.Ability;
import sk.malajter.ability.HeroAbilityManager;
import sk.malajter.constant.Constants;
import sk.malajter.domain.Enemy;
import sk.malajter.domain.Hero;
import sk.malajter.domain.LoadedGame;
import sk.malajter.domain.Weapon;
import sk.malajter.utility.EnemyGenerator;
import sk.malajter.utility.InputUtils;
import sk.malajter.utility.PrintUtils;
import sk.malajter.utility.WeaponGenerator;

import java.util.List;
import java.util.Map;

public class GameManager {

    private Hero hero;

    private final HeroAbilityManager heroAbilityManager;

    private int currentLevel;

    private final FileService fileService;

    private final BattleService battleService;

    private final Map<Integer, Enemy> enemiesByLevel;

    private final List<Weapon> weapons;

    public GameManager() {
        this.hero = new Hero("");
        // An option to start game in Main is put the startGame method to constructor of GameManager:
        //startGame();
        this.heroAbilityManager = new HeroAbilityManager(this.hero);
        this.currentLevel = Constants.INITIAL_LEVEL;
        this.fileService = new FileService();
        this.battleService = new BattleService();
        this.enemiesByLevel = EnemyGenerator.createEnemies();
        this.weapons = WeaponGenerator.generateWeapons();
    }

    public void startGame() throws InterruptedException {
        this.initGame();

        while (this.currentLevel <= this.enemiesByLevel.size()) {
            final Enemy enemy = this.enemiesByLevel.get(this.currentLevel);
            System.out.println("0. Fight " + enemy.getName() + " (Level " + this.currentLevel + ")");
            System.out.println("1. Upgrade abilities (" + hero.getHeroAvailablePoints() + " points to spend.)");
            System.out.println("2. Save game.");
            System.out.println("3. Exit game.");

            final int choice = InputUtils.readInt();
            switch (choice) {
                case 0 -> {
                    if (this.battleService.isHeroReadyToBattle(this.hero, enemy)) {
                        final int heroHealthBeforeBattle = this.hero.getAbilities().get(Ability.HEALTH);

                        final boolean hasHeroWon = this.battleService.battle(this.hero, enemy);
                        if (hasHeroWon) {
                            PrintUtils.printDivider();
                            System.out.println("You have won this battle! You have gained " + this.currentLevel + " ability points.");
                            this.hero.updateHeroAvailablePoints(this.currentLevel);
                            this.currentLevel++;

                            // let choose the hero some weapon
                            chooseWeapon();

                        } else {
                            System.out.println("You have lost.");
                        }

                        // restore health
                        this.hero.setAbility(Ability.HEALTH, heroHealthBeforeBattle);
                        System.out.println("You have full health now.");
                        PrintUtils.printDivider();
                    }
                }
                case 1 -> {
                    this.upgradeAbilities();
                }
                case 2 -> {
                    this.fileService.saveGame(this.hero, this.currentLevel);
                }
                case 3 -> {
                    System.out.println("Are you sure?");
                    System.out.println("0. No");
                    System.out.println("1. Yes");
                    final int exitChoice = InputUtils.readInt();
                    if (exitChoice == 1) {
                        System.out.println("Bye!");
                        return;
                    }
                    System.out.println("Continuing....");
                    PrintUtils.printDivider();
                }
                default -> System.out.println("Invalid input.");
            }
        }
        System.out.println("Congratulation! You won the game.");
    }

    private void upgradeAbilities() {
        System.out.println("Your abilities are:");
        PrintUtils.printAbilities(this.hero);

        System.out.println("0. Go back.");
        System.out.println("1. Spend points. (" + hero.getHeroAvailablePoints() + " points to spend.)");
        System.out.println("2. Remove points.");

        final int choice = InputUtils.readInt();
        switch (choice) {
            case 0 -> {}
            case 1 -> this.heroAbilityManager.spendHeroAvailablePoints();
            case 2 -> this.heroAbilityManager.removeHeroAvailablePoints();
            case 3 -> System.out.println("Invalid choice.");
        }
    }

    private void initGame() {
        System.out.println("Welcome to the Gladiatus game!");

        System.out.println("0. Start new game.");
        System.out.println("1. Load game.");

        final int choice = InputUtils.readInt();
        switch (choice) {
            case 0 -> {
                System.out.println("Let's go then.");
            }
            case 1 -> {
                final LoadedGame loadGame = fileService.loadGame();
                if (loadGame != null) {
                    this.hero = loadGame.getHero();
                    this.currentLevel = loadGame.getLevel();
                    return;
                }
            }
            default -> System.out.println("Invalid choice.");
        }

        System.out.println("Enter your name: ");
        final String name = InputUtils.readString();
        this.hero.setName(name);
        System.out.println("Hello " + hero.getName() + ". Let's start the game!");
        PrintUtils.printDivider();
        System.out.println("\nYour abilities: ");
        PrintUtils.printAbilities(hero);
        PrintUtils.printDivider();
        this.heroAbilityManager.spendHeroAvailablePoints();
    }

    private void printWeapons() {
        for (Weapon weapon : this.weapons) {
            System.out.println(weapon.getName() + " boost your ability " + weapon.getBoost() + " times.");
        }
        System.out.println();
    }

    private void chooseWeapon() {
        while (true) {
            System.out.println("Do you want to choose some weapon?");
            System.out.println("0. No. Go back.");
            System.out.println("1. Description.");
            System.out.println("2. Yes.");

            final int choice = InputUtils.readInt();
            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> {
                    printWeapons();
                }
                case 2 -> {
                    System.out.println("Choose weapon.");
                    for (int i = 0; i < this.weapons.size(); i++) {
                        System.out.println(i + ". " + this.weapons.get(i).getName());
                    }
                    int weapon = InputUtils.readInt();
                    if (weapon >= 0 && weapon < this.weapons.size()) {
                        Weapon selectedWeapon  = this.weapons.get(weapon);
                        this.hero.boostHeroAbilityPoints(Ability.ATTACK, selectedWeapon.getBoost());
                        System.out.println("Ability " + Ability.ATTACK + " was boosted " + selectedWeapon.getBoost() + " times.");
                        return;
                    } else {
                        System.out.println("Invalid input. Choose again.");
                    }
                }
                default -> System.out.println("Invalid input.");
            }
        }
    }
}
