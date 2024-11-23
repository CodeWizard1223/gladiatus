package sk.malajter.domain;

import sk.malajter.ability.Ability;
import sk.malajter.constant.Constant;

import java.util.HashMap;
import java.util.Map;

public class Hero {
    private String name;
    private Map<Ability, Integer> abilities;
    private int heroAvailablePoints;

    public Hero(String name) {
        this.name = name;
        this.abilities = this.geInitialAbilities();
        this.heroAvailablePoints = Constant.INITIAL_ABILITY_POINTS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Ability, Integer> getAbilities() {
        return abilities;
    }

    public int getHeroAvailablePoints() {
        return heroAvailablePoints;
    }

    public void updateAbility(Ability ability, int delta) {
        if (ability.equals(Ability.HEALTH)) {
            this.abilities.put(ability, this.abilities.get(ability) + delta * Constant.HEALTH_OF_ONE_POINT);
        } else {
            this.abilities.put(ability, this.abilities.get(ability) + delta);
        }
    }

    public void updateAvailablePoints(int delta) {
        this.heroAvailablePoints += delta;
    }

    // method for creating hashMap of Abilities and their values
    private Map<Ability, Integer> geInitialAbilities() {
        return new HashMap<>(Map.of(
              Ability.ATTACK, 1,
              Ability.DEFENCE, 1,
                Ability.DEXTERITY, 1,
                Ability.SKILL, 1,
                Ability.LUCK, 1,
                Ability.HEALTH, 50
        ));
    }
}