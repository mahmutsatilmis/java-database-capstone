import { API_BASE_URL } from "../config/config.js";
const PATIENT_API = `${API_BASE_URL}/patient`;

export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });
    const result = await response.json();
    if (!response.ok) {
      throw new Error(result.message || "Failed to sign up");
    }
    return { success: response.ok, message: result.message };
  } catch (error) {
    console.error("Error :: patientSignup ::", error);
    return { success: false, message: error.message };
  }
}

export async function patientLogin(data) {
  return await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();
    if (response.ok) return data.patient;
    return null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

export async function getPatientAppointments(id, token) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${token}`);
    const data = await response.json();
    if (response.ok) {
      return data.appointments || [];
    }
    return [];
  } catch (error) {
    console.error("Error fetching patient appointments:", error);
    return [];
  }
}

export async function filterAppointments(condition, name, token) {
  try {
    const sanitizedCondition = condition || "all";
    const sanitizedName = name || "all";
    const response = await fetch(`${PATIENT_API}/filter/${sanitizedCondition}/${sanitizedName}/${token}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      return await response.json();
    }
    return { appointments: [] };
  } catch (error) {
    console.error("Error filtering appointments:", error);
    return { appointments: [] };
  }
}