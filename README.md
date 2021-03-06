# Deutsche garage

It`s an android application designed for [Deutsche Garage](http://d-garage.com.ua/) company. This application helps 
to register the goods when a new product arrives at the warehouse.

## Functional

- scanning barcode of car spare part that arrived to warehouse and open description on [Bimmercat](http://bimmercat.com/).
- parse [Bimmercat](http://bimmercat.com/) and collect information about car spare parts.
- creating a record of accounting for the arrival of spare parts to the warehouse and automatically adding them 
  to it during scanning, and save this information to DB.
- view records and their details, delete them, and share their details in text format.

## Release notes

- ### **_v1.3.1:_**
  - changed spare parts collecting information from vin to part number
  - changed field in Part entity from vin to partNumber
  - added db migration from v2 to v3
  - added CRLF fix for the shared note text description which sharing to Windows

- ### **_v1.3:_**
  - added ability to choose the quantity of spare parts at one scanning parcel
  - restructured database, and relationship between tables:
    - Part table now containing only unique entities by vin
    - Note now relied on list of NoteItems
    - add NoteItem entity which contain foreign keys to Note and Part, and a quantity of Part in one Note
  - refactored and improved repositories logic
  - added db migration from v1 to v2

- ### **_v1.2:_**
  - refactored design of application
  - created database
  - created a recording of accounting for the arrival of spare parts to the warehouse
  - created an activity where records can be viewed, shared, and deleted
  - fix, and some improvement of project code from previous versions

- ### **_v1.1:_**
  - replaced parsing html part description and showing results by redirecting user direct to website according to customer requirement
  - refactored code according to changes and delete useless files

- ### **_v1.0:_**
  - created first version of application
  - created html parser 
  - created barcode scanner service
  
## Screenshot

<img alt="main menu" src="screenshot/1623959727427.jpg" width="216"/>  <img alt="notes menu" src="screenshot/1623959727425.jpg" width="216"/>  <img alt="note menu" src="screenshot/1623959727426.jpg" width="216"/>
<img alt="scanner" src="screenshot/1623959727420.jpg" width="216"/>  <img alt="new record" src="screenshot/1623959727419.jpg" width="216"/>  <img alt="browser" src="screenshot/1623959727418.jpg" width="216"/>