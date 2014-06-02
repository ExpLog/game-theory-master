lazy val LoadTest = config("test-load") extend Test
lazy val loadTestSettings : Seq[Setting[_]] = inConfig(LoadTest)(Defaults.testSettings ++ Seq(sourceDirectory in LoadTest <<= (sourceDirectory in LoadTest)(_ / ".." / "test-load")))

lazy val root = Project(...)
  .settings(ideaExtraTestConfigurations := Seq(LoadTest) :: Nil)
  .configs( LoadTest )
  .settings( loadTestSettings : _*)