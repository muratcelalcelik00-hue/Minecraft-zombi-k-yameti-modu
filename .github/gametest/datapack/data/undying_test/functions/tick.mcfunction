# One tick of the test schedule. Everything is staged on a counter because the
# things under test — sun exposure, pathing toward a sound, infection progress —
# only exist over time.
scoreboard players add #t ut 1

execute if score #t ut matches 1 run function undying_test:setup
execute if score #t ut matches 20 run function undying_test:spawn
execute if score #t ut matches 60 run function undying_test:check_attrs
execute if score #t ut matches 100 run function undying_test:boom
execute if score #t ut matches 400 run function undying_test:check_sense
execute if score #t ut matches 560 run function undying_test:check_sun
execute if score #t ut matches 580 run function undying_test:infect
execute if score #t ut matches 720 run function undying_test:check_infection
execute if score #t ut matches 740 run function undying_test:finish
