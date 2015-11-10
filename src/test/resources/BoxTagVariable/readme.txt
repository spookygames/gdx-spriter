This scml Includes the following important metadata related things:

1) The Slide animation uses a numeric(float) based variable in the main metadata which could be used to control the x-velocity of the character in the game engine. Ideally, the value would tween between key-frames which have a different value set for the x-velocity variable.

2) The Punch has a Collision box called "AttackBox" which itself has a numeric value set up in its private metadata to keep track of the current damage the hitbox should inflict on enemies. The value should tween between keyframes.

3) The Punch animation also includes a tag in the main metadata which is used as "state machine data" to tell the game engine whether or not the character is currently "stuck in a move" (can not respond to player input) or not.