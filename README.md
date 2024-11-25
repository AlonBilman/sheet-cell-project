# **SheetCell Project: A Java-Based Spreadsheet Application with Real-Time Collaboration**

## **Project Overview**

**SheetCell Project** is a Java-based spreadsheet application that allows multiple users to work on the same document at
the same time.

It features a powerful backend and a user-friendly JavaFX interface for managing data, formulas, and real-time updates.

The system uses a client-server model to enable smooth collaboration and communication between users, making it easy to
edit and share spreadsheets efficiently.

---

## **Key Features**

- **Real-Time Collaboration:**
    - Multiple users can edit the same spreadsheet simultaneously, with changes reflecting in real time.
    - Uses a client-server model with communication via HTTP and JSON, enabling efficient and seamless updates.

- **Real-Time Chat:**
    - Integrated **real-time chat** feature allows users to communicate and collaborate while editing the spreadsheet.
    - Enables instant messaging between users, enhancing collaboration and making it easier to discuss changes or share
      feedback.

- **Dynamic Updates and Formula Handling:**
    - Supports complex formulas and real-time updates based on user input.
    - Prevents circular dependencies in formulas, ensuring accurate calculations and smooth user experience.
    - A **Dynamic Change** feature allows users to interactively adjust cell values using a slider, with immediate
      visual updates reflecting on the spreadsheet.

- **Data Visualization:**
    - Users can create dynamic charts (e.g., bar and line charts) to visualize spreadsheet data.
    - Supports sorting, filtering, and chart creation for data analysis directly within the spreadsheet.

- **Role-Based Access Control (RBAC):**
    - User roles include **Owner**, **Reader**, and **Writer** with granular control over spreadsheet access.
    - **Owners** manage permissions, while **Readers** have view-only access and **Writers** can edit the sheet.
    - Permission requests and approvals are easily managed through the UI.

- **User Interface:**
    - Built with **JavaFX**, the UI is intuitive and responsive, offering an interactive experience for viewing,
      editing, and managing spreadsheets.
    - Supports scrollable rows and columns, making it easy to navigate large datasets.
    - Fully customizable with **CSS** styling and three different themes for a personalized user experience.

- **Version Synchronization:**
    - The system includes version synchronization, alerting users if their spreadsheet is out of sync with the server.

## **Final Stage: Client-Server Architecture**

In the final stage of the **SheetCell** project, the system was enhanced with a **client-server architecture** that
enables real-time collaboration between multiple users. The client and server communicate through **HTTP requests**,
where the client uses **OkHttp** for handling **JSON** data exchange. This architecture was designed to ensure efficient
and smooth data transfer, enabling users to edit the spreadsheet collaboratively.

### **Server-Side Features:**

- **Core Manager:**
    - Handles all sheet operations, such as creating, editing, and managing cells.
    - Manages the server-side logic and synchronization of sheet data between users.

- **Permission System:**
    - Owners can control user access to spreadsheets by assigning **Reader** or **Writer** permissions.
    - Permissions are managed through the server, and users must request permission to edit a sheet if they do not have
      the appropriate role.
    - The permission system allows Owners to approve or deny access requests for greater control.

- **Real-Time Updates:**
    - The server manages real-time updates for all connected users. When one user modifies a cell, the change is
      instantly reflected for other users with access to that sheet.
    - The **Load Update** feature notifies users if a new version of the sheet is available, prompting them to refresh
      the sheet for the latest changes.

### **Client-Side Features:**

- **User Interface (UI):**
    - The client-side application is powered by **JavaFX**, offering an intuitive, responsive interface for managing
      spreadsheets.
    - Users can interact with the sheet through various UI components such as buttons, sliders, and data tables.
    - The UI is designed to allow users to easily upload sheets, view and edit data, and request permissions for
      accessing or modifying sheets.

- **Dynamic Change Slider:**
    - The **Dynamic Change** feature allows users to modify the value of a cell using a slider and instantly observe the
      changes in the data or charts that depend on that cell.
    - This real-time interactivity improves user experience, making it easier to experiment with data.

- **Chat System:**
    - The integrated **chat system** enables users to communicate with others in real-time, allowing better
      collaboration and discussion while working on the same spreadsheet.

---

## **Technologies Used**

### **Backend (Server Side):**

- **Java**: Used to implement the server-side logic and handle HTTP requests.
- **Apache Tomcat**: Deployed for running the server, handling incoming HTTP requests, and serving the client
  application.
- **Gson**: Utilized for parsing and serializing JSON data in server-client communication.
- **JAXB**: Used for parsing and handling **XML** data, which is critical for loading spreadsheet information.

### **Frontend (Client Side):**

- **Java**: The client-side application logic is implemented using Java.
- **JavaFX**: Used for building the graphical user interface (GUI) of the application.
- **FXML**: Used in combination with JavaFX for defining the layout of the UI in a structured, maintainable manner.
- **OkHttp**: Used for handling HTTP requests from the client to the server.
- **Gson**: Employed for parsing and serializing JSON data between the client and server.

### **Data Persistence and Communication:**

- **In-memory data storage**: Utilized for fast access to sheet data during runtime.
- **HTTP & JSON**: The server-client communication is handled over HTTP with **OkHttp** for requests and **Gson** for
  JSON parsing/serialization.

---

## **Getting Started**

**Clone the Repository:**

   ```bash
   git clone https://github.com/AlonBilman/sheet-cell-project
``` 

### Navigate to the Folders:

- **server/:** Contains server-side components for handling sheet data and user permissions.
- **client/:** Contains the client-side application with the JavaFX UI.

### Set Up the Environment:

- Install **Java JDK 21** and **Apache Tomcat** for server deployment.

  Please refer to the [README.md](https://github.com/AlonBilman/sheet-cell-project/tree/master/ServerApp) for detailed
  instructions on installation and configuration.
- The client application includes **JavaFX**, which is bundled with the project.

  Further setup details can be found in
  the [README.md](https://github.com/AlonBilman/sheet-cell-project/tree/master/ClientApp).

### Run the Application:

- Deploy the server on **Tomcat** and run the client using the provided scripts.

---
# Thank you for reading!  