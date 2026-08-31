# User Story Template
**Title:**
_As a [user role], I want [feature/goal], so that [reason]._
**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]
**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort]
**Notes:**
- [Additional information]

## Admin User Stories
## 1. Admin Login
**Title:** Admin Login
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._
**Acceptance Criteria:**
1. The admin can enter a username and password.
2. Valid credentials grant access to the admin dashboard.
3. Invalid credentials display an error message.
**Priority:** High
**Story Points:** 3
**Notes:**
- Authentication should be secure.

## 2. Admin Logout
**Title:** Admin Logout
_As an admin, I want to log out of the portal, so that I can protect system access._
**Acceptance Criteria:**
1. The admin can log out from the dashboard.
2. The current session is terminated.
3. Protected pages require logging in again.
**Priority:** High
**Story Points:** 1
**Notes:**
- Prevent unauthorized access after logout.

## 3. Add Doctor
**Title:** Add Doctor
_As an admin, I want to add doctors to the portal, so that new doctors can be managed in the system._
**Acceptance Criteria:**
1. The admin can enter doctor details.
2. The new doctor profile is saved successfully.
3. The doctor appears in the doctor list.
**Priority:** High
**Story Points:** 3
**Notes:**
- Required fields should be validated.

## 4. Delete Doctor Profile
**Title:** Delete Doctor Profile
_As an admin, I want to delete a doctor's profile from the portal, so that inactive doctors can be removed._
**Acceptance Criteria:**
1. The admin can select a doctor profile.
2. A confirmation is required before deletion.
3. The doctor is removed from the system.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Prevent accidental deletions.

## 5. View Monthly Appointment Statistics
**Title:** View Monthly Appointment Statistics
_As an admin, I want to run a MySQL stored procedure to view the number of appointments per month, so that I can track platform usage._
**Acceptance Criteria:**
1. The stored procedure runs successfully.
2. Monthly appointment totals are returned.
3. The results can be reviewed for reporting purposes.
**Priority:** Medium
**Story Points:** 5
**Notes:**
- The stored procedure should use the existing DB data.

## Doctor User Stories
## 1. Doctor Login
**Title:** Doctor Login
_As a doctor, I want to log into the portal, so that I can manage my appointments._
**Acceptance Criteria:**
1. The doctor can enter a username and password.
2. Valid credentials grant access to the doctor dashboard.
3. Invalid credentials display an error message.
**Priority:** High
**Story Points:** 3
**Notes:**
- Authentication should be secure.

## 2. Doctor Logout
**Title:** Doctor Logout
_As a doctor, I want to log out of the portal, so that I can protect my data._
**Acceptance Criteria:**
1. The doctor can log out from the dashboard.
2. The session is terminated after logout.
3. Protected pages require logging in again.
**Priority:** High
**Story Points:** 1
**Notes:**
- Prevent unauthorized access after logout.

## 3. View Appointment Calendar
**Title:** View Appointment Calendar
_As a doctor, I want to view my appointment calendar, so that I can stay organized._
**Acceptance Criteria:**
1. The calendar displays upcoming appointments.
2. Appointment dates and times are clearly visible.
3. The calendar updates when new appointments are booked.
**Priority:** High
**Story Points:** 3
**Notes:**
- Only the logged-in doctor's appointments should be displayed.

## 4. Mark Unavailability
**Title:** Mark Unavailability
_As a doctor, I want to mark my unavailable dates and times, so that patients can only book available slots._
**Acceptance Criteria:**
1. The doctor can select unavailable dates or time slots.
2. Unavailable slots cannot be booked by patients.
3. Availability updates are reflected immediately.
**Priority:** Medium
**Story Points:** 5
**Notes:**
- Existing appointments should not be affected.

## 5. Update Doctor Profile
**Title:** Update Doctor Profile
_As a doctor, I want to update my specialization and contact information, so that patients can see accurate information._
**Acceptance Criteria:**
1. The doctor can edit specialization details.
2. The doctor can update contact information.
3. Changes are saved and visible to patients.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Profile changes should persist after logout.

## Patient User Stories
## 1. View Doctor List
**Title:** View Doctor List
_As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering._
**Acceptance Criteria:**
1. The doctor list is visible without authentication.
2. Each doctor's name and specialization are displayed.
3. The list loads successfully.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Public access should not expose sensitive information.

## 2. Patient Sign Up
**Title:** Patient Sign Up
_As a patient, I want to sign up using my email and password, so that I can book appointments._
**Acceptance Criteria:**
1. The patient can register with an email and password.
2. Required fields are validated.
3. The account is created successfully.
**Priority:** High
**Story Points:** 3
**Notes:**
- Email addresses should be unique.

## 3. Patient Login
**Title:** Patient Login
_As a patient, I want to log into the portal, so that I can manage my bookings._
**Acceptance Criteria:**
1. The patient can enter valid credentials.
2. Successful login opens the patient dashboard.
3. Invalid credentials display an error message.
**Priority:** High
**Story Points:** 3
**Notes:**
- Authentication should be secure.

## 4. Book an Appointment
**Title:** Book an Appointment
_As a patient, I want to book a one-hour appointment with a doctor, so that I can receive a consultation._
**Acceptance Criteria:**
1. The patient can select an available doctor.
2. The patient can choose an available one-hour time slot.
3. The appointment is confirmed after booking.
**Priority:** High
**Story Points:** 5
**Notes:**
- Only available time slots should be selectable.

## 5. View Upcoming Appointments
**Title:** View Upcoming Appointments
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._
**Acceptance Criteria:**
1. Upcoming appointments are displayed.
2. Appointment date, time, and doctor information are visible.
3. The list updates after a new booking.
**Priority:** Medium
**Story Points:** 2
**Notes:**
- Only the logged-in patient's appointments should be shown.
