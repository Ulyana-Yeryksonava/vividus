Description: Integration tests for variables

Meta:
    @group variables

Scenario: Verify default variables
Then `1` is = `${key:1}`
Then `2` is = `${list[0]:2}`
Then `3` is = `${map.key:3}`
Then `4` is = `${list[0].key:4}`