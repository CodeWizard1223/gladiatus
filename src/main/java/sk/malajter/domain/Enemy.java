package sk.malajter.domain;

import sk.malajter.ability.Ability;

import java.util.Map;

public class Enemy extends GameCharacter{

    public Enemy(String name, Map<Ability, Integer> abilities) {
        super(name, abilities);
    }
}