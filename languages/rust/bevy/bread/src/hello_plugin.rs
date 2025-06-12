use bevy::prelude::*;

pub struct HelloPlugin;
#[derive(Component)]
struct Person;

#[derive(Component)]
struct Name(String);

fn add_people(mut commands: Commands) {
    commands.spawn((Person, Name("Don Proctor".to_string())));
    commands.spawn((Person, Name("Enzo Um".to_string())));
    commands.spawn((Person, Name("Naty Bleu".to_string())));
}
fn hello_world() {
    println!("hello world!");
}

fn greet_people(query: Query<&Name, With<Person>>) {
    for name in &query {
        println!("hello {}!", name.0);
    }
}

fn update_people(mut query: Query<&mut Name, With<Person>>) {
    for mut name in &mut query {
        if name.0 == "Don Proctor" {
            name.0 = "Donna Proctor".to_string();
            break
        }
    }
}

impl Plugin for HelloPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(Startup, add_people);
        // .chain make sure the order for update_people and greet_people happens, otherwise can greet
        // and name change after
        app.add_systems(Update, (hello_world, (update_people, greet_people).chain()));
    }
}