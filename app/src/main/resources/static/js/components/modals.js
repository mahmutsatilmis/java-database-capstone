// modals.js
export function openModal(type) {
  let modalContent = '';

  if (type === 'addDoctor') {
    modalContent = `
      <div class="modal-card">
        <h2>Add Doctor</h2>
        <div class="field-group">
          <label>Doctor Name</label>
          <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
        </div>
        <div class="field-group">
          <label>Specialization</label>
          <select id="specialization" class="input-field select-dropdown">
            <option value="">Select specialization</option>
            <option value="Cardiologist">Cardiologist</option>
            <option value="Dermatologist">Dermatologist</option>
            <option value="Neurologist">Neurologist</option>
            <option value="Pediatrician">Pediatrician</option>
            <option value="Orthopedist">Orthopedist</option>
            <option value="Gynecologist">Gynecologist</option>
            <option value="Psychiatrist">Psychiatrist</option>
            <option value="Dentist">Dentist</option>
            <option value="Ophthalmologist">Ophthalmologist</option>
            <option value="ENT Specialist">ENT Specialist</option>
            <option value="Urologist">Urologist</option>
            <option value="Oncologist">Oncologist</option>
            <option value="Gastroenterologist">Gastroenterologist</option>
            <option value="General Physician">General Physician</option>
          </select>
        </div>
        <div class="field-group">
          <label>Email</label>
          <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
        </div>
        <div class="field-group">
          <label>Password</label>
          <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
        </div>
        <div class="field-group">
          <label>Phone Number</label>
          <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
        </div>
        <div class="availability-container">
          <label class="availabilityLabel">Select available time slots</label>
          <div class="checkbox-group">
            <label class="checkbox-item"><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
            <label class="checkbox-item"><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
            <label class="checkbox-item"><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
            <label class="checkbox-item"><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
          </div>
        </div>
        <button class="dashboard-btn primary-btn" id="saveDoctorBtn">Save Doctor</button>
      </div>
    `;
  } else if (type === 'adminLogin') {
    modalContent = `
      <div class="modal-card" style="width: 100%; max-width: 400px; padding: 24px; display: flex; flex-direction: column; align-items: center;">
        <h2 style="margin-bottom: 20px; text-align: center; color: #013b3d;">Admin Login</h2>
        <input type="text" id="username" name="username" placeholder="Username" class="input-field" style="width: 100%; margin-bottom: 15px;">
        <input type="password" id="password" name="password" placeholder="Password" class="input-field" style="width: 100%; margin-bottom: 20px;">
        <button class="dashboard-btn" id="adminLoginBtn" style="width: 100%; margin-top: 0;">Login</button>
      </div>
    `;
  } else if (type === 'doctorLogin') {
    modalContent = `
      <div class="modal-card" style="width: 100%; max-width: 400px; padding: 24px; display: flex; flex-direction: column; align-items: center;">
        <h2 style="margin-bottom: 20px; text-align: center; color: #013b3d;">Doctor Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field" style="width: 100%; margin-bottom: 15px;">
        <input type="password" id="password" placeholder="Password" class="input-field" style="width: 100%; margin-bottom: 20px;">
        <button class="dashboard-btn" id="doctorLoginBtn" style="width: 100%; margin-top: 0;">Login</button>
      </div>
    `;
  } else if (type === 'patientLogin') {
    modalContent = `
      <div class="modal-card" style="width: 100%; max-width: 400px; padding: 24px; display: flex; flex-direction: column; align-items: center;">
        <h2 style="margin-bottom: 20px; text-align: center; color: #013b3d;">Patient Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field" style="width: 100%; margin-bottom: 15px;">
        <input type="password" id="password" placeholder="Password" class="input-field" style="width: 100%; margin-bottom: 20px;">
        <button class="dashboard-btn" id="loginBtn" style="width: 100%; margin-top: 0;">Login</button>
      </div>
    `;
  } else if (type === 'patientSignup') {
    modalContent = `
      <div class="modal-card" style="width: 100%; max-width: 440px; padding: 24px; display: flex; flex-direction: column; align-items: center;">
        <h2 style="margin-bottom: 20px; text-align: center; color: #013b3d;">Patient Signup</h2>
        <input type="text" id="name" placeholder="Name" class="input-field" style="width: 100%; margin-bottom: 12px;">
        <input type="email" id="email" placeholder="Email" class="input-field" style="width: 100%; margin-bottom: 12px;">
        <input type="password" id="password" placeholder="Password" class="input-field" style="width: 100%; margin-bottom: 12px;">
        <input type="text" id="phone" placeholder="Phone (10 digits)" class="input-field" style="width: 100%; margin-bottom: 12px;">
        <input type="text" id="address" placeholder="Address" class="input-field" style="width: 100%; margin-bottom: 20px;">
        <button class="dashboard-btn" id="signupBtn" style="width: 100%; margin-top: 0;">Signup</button>
      </div>
    `;
  }

  const modal = document.getElementById('modal');
  const modalBody = document.getElementById('modal-body');
  if (!modal || !modalBody) return;

  modalBody.innerHTML = modalContent;
  modal.style.display = "";
  modal.classList.add('active');

  const closeBtn = document.getElementById('closeModal');
  if (closeBtn) {
    closeBtn.onclick = () => {
      modal.classList.remove('active');
      modal.style.display = "none";
    };
  }

  if (type === 'addDoctor') {
    const saveBtn = document.getElementById('saveDoctorBtn');
    if (saveBtn && typeof window.adminAddDoctor === 'function') {
      saveBtn.addEventListener('click', window.adminAddDoctor);
    }
  }

  if (type === 'adminLogin') {
    const btn = document.getElementById('adminLoginBtn');
    if (btn && typeof window.adminLoginHandler === 'function') {
      btn.addEventListener('click', window.adminLoginHandler);
    }
  }

  if (type === 'doctorLogin') {
    const btn = document.getElementById('doctorLoginBtn');
    if (btn && typeof window.doctorLoginHandler === 'function') {
      btn.addEventListener('click', window.doctorLoginHandler);
    }
  }

  if (type === 'patientLogin') {
    const btn = document.getElementById('loginBtn');
    if (btn) {
      btn.addEventListener('click', () => {
        if (typeof window.patientLoginHandler === 'function') {
          window.patientLoginHandler();
        } else if (typeof window.loginPatient === 'function') {
          window.loginPatient();
        }
      });
    }
  }

  if (type === 'patientSignup') {
    const btn = document.getElementById('signupBtn');
    if (btn && typeof window.signupPatient === 'function') {
      btn.addEventListener('click', window.signupPatient);
    }
  }
}