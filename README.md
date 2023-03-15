# pps-pvzlike

![license](https://img.shields.io/github/license/PaoloPenazzi/pps-pvzlike)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=PaoloPenazzi_pps-pvzlike&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=PaoloPenazzi_pps-pvzlike)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=PaoloPenazzi_pps-pvzlike&metric=bugs)](https://sonarcloud.io/dashboard?id=PaoloPenazzi_pps-pvzlike)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=PaoloPenazzi_pps-pvzlike&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=PaoloPenazzi_pps-pvzlike)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=PaoloPenazzi_pps-pvzlike&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=PaoloPenazzi_pps-pvzlike)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=PaoloPenazzi_pps-pvzlike&metric=security_rating)](https://sonarcloud.io/dashboard?id=PaoloPenazzi_pps-pvzlike)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=PaoloPenazzi_pps-pvzlike&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=PaoloPenazzi_pps-pvzlike)

`pps-pvzlike` is a reprodution of the single-player game [Plants vs. Zombies](https://www.ea.com/it-it/games/plants-vs-zombies/plants-vs-zombies)
In the game, players try to prevent zombies from reaching the end of a path by placing plants that can kill the zombies. The zombies' waves come in different rounds with increasing level of difficulty.

This project is developed for academic purposes for the course `Paradigmi di Programmazione e Sviluppo` of the master's degree `Ingegneria e Scienze Informatiche` of `University of Bologna` under the academic year 2021/2022.

## Requirements
The following dependencies are required to play `pps-pvzlike`:
- Sbt version 1.7.1 or newer
- Scala version 3.2.0 or newer
- Java version 11 or newer

## Usage

You can find the latest `jar` of the application inside the [`GitHub Release section`](https://github.com/PaoloPenazzi/pps-pvzlike/releases).

To execute the application, simply run:

```
$ java -jar `path-to-downloaded-jar`
```

Alternatively, you can clone the repository and execute the following commands to generate the `jar` executable file:

```
$ sbt compile
$ sbt assembly
```

## Test

You can execute tests with the command:

```
$ sbt test
```
## Report
You can download the updated report on the developed project [here](https://github.com/PaoloPenazzi/pps-pvzlike/releases/tag/0.4.0). 

## Authors
- Alpi Davide ([DavideAlpiGit](https://github.com/DavideAlpiGit))
- Foschini Francesco ([FrancescoFoschini](https://github.com/))
- Parrinello Angelo ([AngeloParrinello](https://github.com/AngeloParrinello))
- Penazzi Paolo ([PaoloPenazzi](https://github.com/PaoloPenazzi))
