// https://github.com/bevyengine/bevy/blob/main/examples/ecs/ecs_guide.rs

use bevy::{
    app::{AppExit, ScheduleRunnerPlugin},
    prelude::*,
};
use core::time::Duration;
use rand::random;
use rand::Rng;
use std::fmt;
#[derive(Component)]
struct Player {
    name: String,
}
#[derive(Component)]
struct Score {
    value: usize,
}
#[derive(Component)]
enum PlayerStreak {
    Hot(usize),
    None,
    Cold(usize),
}

impl fmt::Display for PlayerStreak {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            PlayerStreak::Hot(n) => write!(f, "{n} round hot streak"),
            PlayerStreak::None => write!(f, "0 round streak"),
            PlayerStreak::Cold(n) => write!(f, "{n} round cold streak"),
        }
    }
}

#[derive(Resource, Default)]
struct GameState {
    current_round: usize,
    total_players: usize,
    winning_player: Option<String>,
}

#[derive(Resource)]
struct GameRules {
    winning_score: usize,
    max_rounds: usize,
    max_players: usize,
}

fn print_message_system() {
    let mut rng = rand::rng();
    let message = match rng.random_range(0..3) {
        0 => "This game is fun",
        1 => "Not going anywhere",
        _ => "Cry baby, cry",
    };
    println!("{}", message);
}

fn new_round_system(game_rules: Res<GameRules>, mut game_state: ResMut<GameState>) {
    game_state.current_round += 1;
    println!(
        "Begin round {} of {}",
        game_state.current_round, game_rules.max_rounds
    );
}

fn score_system(mut query: Query<(&Player, &mut Score, &mut PlayerStreak)>) {
    for (player, mut score, mut streak) in &mut query {
        let scored_a_point = random::<bool>();
        if scored_a_point {
            score.value += 1;
            *streak = match *streak {
                PlayerStreak::Hot(n) => PlayerStreak::Hot(n + 1),
                PlayerStreak::Cold(_) | PlayerStreak::None => PlayerStreak::Hot(1),
            };
            println!(
                "{} scored a point! Their score is: {} ({})",
                player.name, score.value, *streak
            );
        } else {
            *streak = match *streak {
                PlayerStreak::Hot(_) | PlayerStreak::None => PlayerStreak::Cold(1),
                PlayerStreak::Cold(n) => PlayerStreak::Cold(n + 1),
            };

            println!(
                "{} did not score a point! Their score is: {} ({})",
                player.name, score.value, *streak
            );
        }
    }

}

fn score_check_system(
    game_rules: Res<GameRules>,
    mut game_state: ResMut<GameState>,
    query: Query<(&Player, &Score)>,
) {
    for (player, score) in &query {
        if score.value == game_rules.winning_score {
            game_state.winning_player = Some(player.name.clone());
        }
    }
}

fn game_over_system(
    game_rules: Res<GameRules>,
    game_state: Res<GameState>,
    mut app_exit_events: EventWriter<AppExit>,
) {
    if let Some(ref player) = game_state.winning_player {
        println!("{player} won the game!");
        app_exit_events.write(AppExit::Success);
    } else if game_state.current_round == game_rules.max_rounds {
        println!("Ran out of rounds. Nobody wins!");
        app_exit_events.write(AppExit::Success);
    }
}

fn startup_system(mut commands: Commands, mut game_state: ResMut<GameState>) {
    let mut rng = rand::rng();
    commands.insert_resource(GameRules {
        max_rounds: rng.random_range(10..50),
        winning_score: rng.random_range(5..20),
        max_players: rng.random_range(2..10),
    });

    commands.spawn_batch(vec![
        (
            Player {
                name: "Alice".to_string(),
            },
            Score { value: 0 },
            PlayerStreak::None,
        ),
        (
            Player {
                name: "Bob".to_string(),
            },
            Score { value: 0 },
            PlayerStreak::None,
        ),
    ]);

    game_state.total_players = 2;
}

fn new_player_system(
    mut commands: Commands,
    game_rules: Res<GameRules>,
    mut game_state: ResMut<GameState>,
) {
    let add_new_player = random::<bool>();
    if add_new_player && game_state.total_players < game_rules.max_players {
        game_state.total_players += 1;
        commands.spawn((
            Player {
                name: format!("Player {}", game_state.total_players),
            },
            Score { value: 0 },
            PlayerStreak::None,
        ));

        println!("Player {} joined the game!", game_state.total_players);
    }
}

fn exclusive_player_system(world: &mut World) {
    let total_players = world.resource_mut::<GameState>().total_players;
    let should_add_player = {
        let game_rules = world.resource::<GameRules>();
        let add_new_player = random::<bool>();
        add_new_player && total_players < game_rules.max_players
    };
    if should_add_player {
        println!("Player {} has joined the game!", total_players + 1);
        world.spawn((
            Player {
                name: format!("Player {}", total_players + 1),
            },
            Score { value: 0 },
            PlayerStreak::None,
        ));

        let mut game_state = world.resource_mut::<GameState>();
        game_state.total_players += 1;
    }
}

fn print_at_end_round(mut counter: Local<u32>) {
    *counter += 1;
    println!("In set 'Last' for the {}th time", *counter);
    println!();
}

#[derive(SystemSet, Debug, Hash, PartialEq, Eq, Clone)]
enum MySystems {
    BeforeRound,
    Round,
    AfterRound,
}

fn main() {
    App::new()
        .init_resource::<GameState>()
        .add_plugins(ScheduleRunnerPlugin::run_loop(Duration::from_secs(5)))
        .add_systems(Startup, startup_system)
        .add_systems(Update, print_message_system)
        .add_systems(Last, print_at_end_round)
        .configure_sets(
            Update,
            (
                MySystems::BeforeRound,
                MySystems::Round,
                MySystems::AfterRound,
            )
                .chain(),
        )
        .add_systems(
            Update,
            (
                (
                    (new_round_system, new_player_system).chain(),
                    exclusive_player_system,
                )
                    .in_set(MySystems::BeforeRound),
                score_system.in_set(MySystems::Round),
                (
                    score_check_system,
                    game_over_system.after(score_check_system),
                )
                    .in_set(MySystems::AfterRound),
            ),
        )
        .run();
}
