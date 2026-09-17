# Nothing below means anything if the subjects are not actually there.
execute if entity @e[tag=ut_a] run say UNDYING-TEST INFO subject-a-present
execute unless entity @e[tag=ut_a] run say UNDYING-TEST FAIL subject-a-missing
execute if entity @e[tag=ut_b] run say UNDYING-TEST INFO subject-b-present
execute unless entity @e[tag=ut_b] run say UNDYING-TEST FAIL subject-b-missing
execute if entity @e[tag=ut_v] run say UNDYING-TEST INFO subject-v-present
execute unless entity @e[tag=ut_v] run say UNDYING-TEST FAIL subject-v-missing
