package com.agonyengine.model.command;

import com.agonyengine.model.actor.Actor;
import com.agonyengine.model.interpret.ActorSameRoom;
import com.agonyengine.model.stomp.GameOutput;
import com.agonyengine.repository.ActorRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Component
public class LookCommand {
    private ActorRepository actorRepository;
    private List<Direction> directions;

    @Inject
    public LookCommand(ActorRepository actorRepository, List<Direction> directions) {
        this.actorRepository = actorRepository;
        this.directions = directions;
    }

    @Transactional
    public void invoke(Actor actor, GameOutput output) {
        List<Actor> actors = actorRepository.findByGameMapAndXAndY(actor.getGameMap(), actor.getX(), actor.getY());

        // TODO game maps will need names
        output.append(String.format("[yellow](%d, %d) %s",
            actor.getX(),
            actor.getY(),
            actor.getGameMap().getId()));

        output.append(String.format("[black](tile=0x%s) You see nothing but the inky void swirling around you.",
            Integer.toHexString(Byte.toUnsignedInt(actor.getGameMap().getTile(actor.getX(), actor.getY())))));

        output.append(directions.stream()
            .filter(direction -> actor.getGameMap().hasTile(actor.getX() + direction.getX(), actor.getY() + direction.getY()))
            .map(Direction::getName)
            .collect(joining(" ", "[cyan]Exits: ", "")));

        actors.stream()
            .filter(target -> !actor.equals(target))
            .forEach(target -> output.append(String.format("[green]%s is here.", target.getName())));
    }

    @Transactional
    public void invoke(Actor actor, GameOutput output, ActorSameRoom target) {
        output.append(String.format("You look at %s.", target.getTarget().getName()));
    }

    @Transactional
    public void invoke(Actor actor, GameOutput output, ActorSameRoom target, ActorSameRoom target2) {
        output.append(String.format("You look at %s, then %s.",
            target.getTarget().getName(),
            target2.getTarget().getName()));
    }
}
