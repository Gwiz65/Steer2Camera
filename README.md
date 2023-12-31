# Steer2Camera
A mod for Wurm Unlimited Client that steers mounts, vehicles and boats towards the camera.

- **Steer2Camera Features:**

  - Works similar to Wurm Online's "Steer mount towards camera" option.
  - Will turn mount/vehicle/boat only when moving forwards, backwards or when autorun is engaged.
  - NOTE: This mod will automatically disable the game option "Rotate Player w/ Mount" because it's counterintuitive.


- **Console Commands:**

| Command | Description |
| :--- | :--- |
| s2c on | enables Steer2Camera (default) |
| s2c off | disables Steer2Camera |
| s2c toggle | toggles Steer2Camera on/off |
| s2c set-margin | displays current accuracy margin value |
| s2c set-margin NUMBER | sets accuracy margin |
| s2c version | displays current version |


- **Accuracy Margin:**

  - The accuracy margin value controls how accurate Steer2Camera is. The value is the amount of error that is acceptable.  Specifically, if the carrier is within the range (in degrees) of (camera angle - accuracyMargin) to (camera angle + accuracyMargin) then it's considered good enough. Increase this value if carrier keeps weaving back and forth for a long time (possibly forever), especially in low framerate or network lag situations. Decrease this value to improve accuracy. Can be changed on the fly with the console command "s2c set-margin" or set permanently in the steer2camera.properties file with the "accuracyMargin" variable.

- **Release Notes:**
  - Release 1.0 - Initial release.
  - Release 1.1 - Added configurable accuracy margin.
  - Release 1.2 - Fixed bug where toon would get stuck sliding left or right after disembarking. Added "version" console command.       
  
    
