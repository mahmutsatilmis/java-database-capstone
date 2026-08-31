## MySQL Database Design
The MySQL database stores the clinic's structured and relational data, including patients, doctors, appointments, and administrators.
### Table: patients
- `id`: INT, Primary Key, AUTO_INCREMENT
- `first_name`: VARCHAR(50), NOT NULL
- `last_name`: VARCHAR(50), NOT NULL
- `email`: VARCHAR(100), UNIQUE, NOT NULL
- `phone`: VARCHAR(20), NOT NULL
- `password`: VARCHAR(255), NOT NULL
### Table: doctors
- `id`: INT, Primary Key, AUTO_INCREMENT
- `first_name`: VARCHAR(50), NOT NULL
- `last_name`: VARCHAR(50), NOT NULL
- `specialization`: VARCHAR(100), NOT NULL
- `email`: VARCHAR(100), UNIQUE, NOT NULL
- `phone`: VARCHAR(20), NOT NULL
### Table: appointments
- `id`: INT, Primary Key, AUTO_INCREMENT
- `doctor_id`: INT, Foreign Key → doctors(id), NOT NULL
- `patient_id`: INT, Foreign Key → patients(id), NOT NULL
- `appointment_time`: DATETIME, NOT NULL
- `status`: INT, NOT NULL (0 = Scheduled, 1 = Completed, 2 = Cancelled)
### Table: admin
- `id`: INT, Primary Key, AUTO_INCREMENT
- `username`: VARCHAR(50), UNIQUE, NOT NULL
- `password`: VARCHAR(255), NOT NULL
## Design Notes
- Email addresses should be unique for patients and doctors.
- Email and phone validation can be handled in the application layer.
- Appointment history should be retained even if a patient account is removed.
- Doctors should not be allowed to have overlapping appointment times.
- Prescriptions should be linked to a specific appointment.



## MongoDB Collection Design
The MongoDB database stores prescription documents, allowing flexible fields such as doctor notes and additional metadata without requiring a fixed schema.
### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 5,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take one tablet every 6 hours after meals.",
  "tags": ["pain", "fever"],
  "metadata": {
    "refillCount": 2,
    "createdAt": "2026-09-01T10:30:00Z"
  }
}
## Design Notes
- patientId, doctorId, and appointmentId are stored instead of embedding full patient or doctor objects.
- tags are stored as an array to make prescriptions easier to categorize.
- metadata is stored as a nested object so additional fields can be added later.
- Each prescription is linked to a specific appointment to maintain a clear medical history.
