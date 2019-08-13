# SmartDrone
SmartDrone is an android application that can generate tones based on user input. This app can analyze the notes the user is playing on their instrument and find the corresponding key. It was created to improve musicians ability to hear chord-scale relationships. 

## Download
[Google Play](https://play.google.com/store/apps/details?id=com.convergencelabstfx.smartdrone&hl=en)

## About
This app was developed by Travis MacDonald as part of a research assistant position for Convergence Lab.

Convergence Lab is a funded research program established by James Hughes, assistant professor at St. Francis Xavier.

## Libraries
SmartDrone was made possible by the following libraries:

* [TarsosDSP](https://github.com/JorenSix/TarsosDSP) for real time audio processing.

* [MidiDriver](https://github.com/billthefarmer/mididriver) for midi synthesis.

## How to Use
This app is best used for practicing static harmony (scales, modal music). Best used with headphones, other wise the app may receive its own output as input.

[Demonstration Video](https://www.youtube.com/watch?v=8_woyE8PgA8&amp;t=)

1. Press <b>START</b> on the main screen and place your phone's microphone by your instrument's output.
2. Play a scale until SmartDrone recognizes what key you are in.
3. Change keys and SmartDrone will hear it.

The delay in key change allows for users to play passing tones without changing the key. This can adjusted in the settings under <b>Active Key Sensitivity</b>. A higher sensitivity makes quicker changes with less accuracy, while a lower sensitivity makes fewer changes with more accuracy.

The <b>Note Length Filter</b> sets how long a note must be heard before it is recognized. A longer filter can help filter out invalid frequencies in noisy environments, but will subsequently require the user to play longer tones in order to be recognized.

Change voicings with the voicing selector and the app will transpose it to any mode. Select a voicing by tapping it, and delete a voicing by holding it. Create a new voicing with the <b>'+'</b> button.
