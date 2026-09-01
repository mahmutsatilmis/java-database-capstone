import { savePrescription, getPrescription } from "./services/prescriptionServices.js";

document.addEventListener("DOMContentLoaded", async () => {
  const savePrescriptionBtn = document.getElementById("savePrescription");
  const patientNameInput = document.getElementById("patientName");
  const medicinesInput = document.getElementById("medicines");
  const dosageInput = document.getElementById("dosage");
  const notesInput = document.getElementById("notes");
  const heading = document.getElementById("heading");

  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  const mode = urlParams.get("mode");
  const token = localStorage.getItem("token");
  const patientName = urlParams.get("patientName");

  if (heading) {
    heading.innerHTML = mode === "view" ? "View <span>Prescription</span>" : "Add <span>Prescription</span>";
  }

  if (patientNameInput && patientName) {
    patientNameInput.value = patientName;
  }

  if (appointmentId && token) {
    try {
      const response = await getPrescription(appointmentId, token);
      const prescriptionData = Array.isArray(response?.prescription)
        ? response.prescription[0]
        : response?.prescription;

      if (prescriptionData) {
        if (patientNameInput) patientNameInput.value = prescriptionData.patientName || "You";
        if (medicinesInput) medicinesInput.value = prescriptionData.medication || "";
        if (dosageInput) dosageInput.value = prescriptionData.dosage || "";
        if (notesInput) notesInput.value = prescriptionData.doctorNotes || "";
      }
    } catch (error) {
      console.warn("No existing prescription found or failed to load:", error);
    }
  }

  if (mode === "view") {
    if (patientNameInput) patientNameInput.disabled = true;
    if (medicinesInput) medicinesInput.disabled = true;
    if (dosageInput) dosageInput.disabled = true;
    if (notesInput) notesInput.disabled = true;
    if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
  }

  if (savePrescriptionBtn) {
    savePrescriptionBtn.addEventListener("click", async (e) => {
      e.preventDefault();

      if (!medicinesInput?.value || !dosageInput?.value) {
        alert("Please provide both medication and dosage.");
        return;
      }

      const prescription = {
        patientName: patientNameInput?.value || "Patient",
        medication: medicinesInput.value,
        dosage: dosageInput.value,
        doctorNotes: notesInput?.value || "",
        appointmentId: Number(appointmentId),
      };

      const { success, message } = await savePrescription(prescription, token);

      if (success) {
        alert("✅ Prescription saved successfully.");
        if (typeof selectRole === "function") {
          selectRole("doctor");
        } else {
          window.location.href = `/doctorDashboard/${token}`;
        }
      } else {
        alert("❌ Failed to save prescription: " + message);
      }
    });
  }
});