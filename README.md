# TimeTracker GeoClient for Android

This is a Android based app to record geo position information. It is part of the TimeTracker Ecosystem.
For more information about this project have a look at http://timetracker.cc.


## Functionality
GPS data can be recorded using the Geo Tracking app. This information where send to a server. If the data can not be sent to a server, it is stored in a database on the device. When the app is active, it sends the recorded data to the server as soon as an Internet connection is established. Since the app is written in Android, it can be very easily integrated as a feature into the existing TimeTracker Application.

Features
* GEO Position Source: Provider Network and/or GPS
* Position recording: By distance, angle and/or time interval
* Data transfer via Rest
* Data format JSON
* Data store on device: MySQL Light


## Team
Development
- Hannes Buchwald ([hannes@timetracker.cc](mailto:hannes@timetracker.cc))

Project Coordination
- Thomas Daum [thomas@timetracker.cc](mailto:thomas@timetracker.cc))


## License
The basic Android application is from Anton Tananaev ([anton@traccar.org](mailto:anton@traccar.org))
and under Apache License, Version 2.0.

The content and extensions of this application are from Hannes Buchwald ([hannes.buchwald@gmail.com](hannes.buchwald@gmail.com))
and under GNU AGPLv3 License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
