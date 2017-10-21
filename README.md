# Audio_trol
This project realizes communication with the outside world through image and sound processing.
## Build
It's maven project. In order to build a project, you need to call 
```
maven package
```
the folder 'target' appears. Folder 'target' contains executable audio-troll-<version>.jar file and some important folders:
* 'natives' contains native dependencies (for OpenCV for example)
* 'resources' contains project resources from 'src/main/resources' folder
* 'lib' contains java dependencies

You can execute audio-troll-<version>.jar file from custom platform with x64 java, if all dependencies and resources in executable file folder.