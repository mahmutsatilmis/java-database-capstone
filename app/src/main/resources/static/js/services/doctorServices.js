import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

export async function getDoctors() {
  try {
    const response = await fetch(`${DOCTOR_API}`);
    if (!response.ok) {
      throw new Error("Unable to fetch doctors");
    }

    const data = await response.json();
    if (Array.isArray(data)) return data;
    if (Array.isArray(data.doctors)) return data.doctors;
    return [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

export async function deleteDoctor(doctorId, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${doctorId}/${token}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(data.message || "Failed to delete doctor");
    }

    return {
      success: true,
      message: data.message || "Doctor deleted successfully.",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: error.message || "Failed to delete doctor.",
    };
  }
}

export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(data.message || "Unable to save doctor");
    }

    return {
      success: true,
      message: data.message || "Doctor saved successfully.",
      doctor: data.doctor || doctor,
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: error.message || "Unable to save doctor.",
    };
  }
}

export async function filterDoctors(name = null, time = null, specialty = null) {
  try {
    const params = new URLSearchParams();
    if (name) params.append("name", name);
    if (time) params.append("time", time);
    if (specialty) params.append("specialty", specialty);

    const response = await fetch(`${DOCTOR_API}/filter?${params.toString()}`);
    if (!response.ok) {
      return { doctors: [] };
    }

    const data = await response.json();
    return { doctors: Array.isArray(data.doctors) ? data.doctors : [] };
  } catch (error) {
    console.error("Error filtering doctors:", error);
    return { doctors: [] };
  }
}

