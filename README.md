# Steer2Camera
A mod for Wurm Unlimited Client that steers mounts, vehicles and boats towards the camera.

- Steer2Camera features:
  - Works similar to Wurm Online's "Steer mount towards camera" option.
  - Will turn mount/vehicle/boat only when moving forwards, backwards or when autorun is engaged.
  - NOTE: This mod will automatically disable the game option "Rotate Player w/ Mount" because it's counterintuitive.

- Console Commands:
  - s2c on               - enables Steer2Camera (default)
  - s2c off              - disables Steer2Camera
  - s2c toggle           - toggles Steer2Camera on/off
  - s2c set-margin       - displays current accuracy margin value
  - s2c set-margin [num] - sets accuracy margin

- Accuracy Margin
  - The accuracy margin value controls how accurate Steer2Camera is. The value is the amount of error that is acceptable. Specifically, if the carrier is within the range (camera angle - accuracyMargin) to (camera angle + accuracyMargin) then it's considered good enough. Increase this value if carrier keeps weaving back and forth for a long time (possibly forever), especially in low framerate or network lag situations. Decrease this value to improve accuracy. Can be changed on the fly with the console command "s2c set-margin" or set permantly in the steer2camera.properties file with the "accuracyMargin" variable.
