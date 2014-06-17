game-theory-master
==================

In order to compile this project with sbt, you first need to:

1. Create a folder named lib.
2. Put a gurobi.jar inside lib (the gurobi api).
3. Open sbt at the project root.
4. Run the compile command.

If you want to generate Intellij Idea project files, you'll simply need to run the command "gen-idea" in sbt.

To package it into a single jar, I recommend using sbt [one-jar](https://github.com/sbt/sbt-onejar) plugin.
