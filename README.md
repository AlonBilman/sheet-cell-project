
# SheetCell Project: Real-Time Collaborative Spreadsheet Application

## Overview
SheetCell is a Java-based spreadsheet tool enabling real-time collaboration. 
Users can edit spreadsheets together, chat live, and visualize data with ease—all in a sleek JavaFX interface.

---

## Key Features
### Real-Time Collaboration
- Seamless updates across multiple users with HTTP and JSON-based communication.

### Integrated Chat
- Chat while editing, making teamwork intuitive and productive.

### Dynamic Updates and Interactive Changes
- **Real-Time Updates:** Automatically recalculates formulas and ensures accurate results.
- **Dynamic Change Feature:** Adjust cell values using a slider and see real-time updates in the spreadsheet and linked charts.

### Data Visualization
- Create dynamic charts (e.g., bar and line charts) directly from spreadsheet data for better analysis.

### Role-Based Permissions
- **Owner:** Full control to edit and manage who can view or edit the sheet.
- **Writer:** Can edit the sheet but can’t manage permissions.
- **Reader:** Can only look at the sheet without making changes.
- If you need edit access, you can send a request to the owner. Owners can quickly approve or deny these requests.

### Responsive UI
- Built with JavaFX, offering three themes and smooth navigation for large datasets.

---

## Architecture
### Client-Server Model
Built for efficiency, with the server managing permissions, updates, and data synchronization, while thread synchronization ensures multiple users can collaborate seamlessly in real time.
- **Server:** Handles core operations and enforces access control.
- **Client:** Features an intuitive UI for seamless interaction with spreadsheets.

---

## Technologies Used
- **Backend:** Java, Apache Tomcat, Gson, JAXB.
- **Frontend:** Java, JavaFX, FXML, OkHttp, Gson.
- **Communication:** HTTP and JSON.

---

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/AlonBilman/sheet-cell-project
```

### Navigate to the Folders
- **ServerApp/**: Contains server-side components for handling sheet data and user permissions.
- **ClientApp/**: Contains the client-side application with the JavaFX UI.

### Set Up the Environment
1. Install **Java JDK 21** and **Apache Tomcat** for server deployment.
2. The client application includes JavaFX, which is bundled with the project.

#### Server setup : [Server README.md](https://github.com/AlonBilman/sheet-cell-project/tree/master/ServerApp)  | Client setup : [Client README.md](https://github.com/AlonBilman/sheet-cell-project/tree/master/ClientApp)

### Run the Application
1. Deploy the server on Tomcat.
2. Run the client using the provided scripts.

---

# Thank you for reading! 🎉
