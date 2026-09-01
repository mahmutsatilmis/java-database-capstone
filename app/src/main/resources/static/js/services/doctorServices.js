import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

export async function getDoctors() {
  try {
    const response = await fetch(`${DOCTOR_API}`);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

export async function filterDoctors(name, time, specialty) {
  try {
    const params = new URLSearchParams();
    if (name && name.trim().length > 0 && name !== "null") params.append("name", name.trim());
    if (time && time.trim().length > 0 && time !== "null") params.append("time", time.trim());
    if (specialty && specialty.trim().length > 0 && specialty !== "null") params.append("specialty", specialty.trim());

    const queryString = params.toString();
    const url = queryString ? `${DOCTOR_API}/filter?${queryString}` : `${DOCTOR_API}`;

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error("Failed to filter doctors");
    }
    return await response.json();
  } catch (error) {
    console.error("Error in filterDoctors:", error);
    return { doctors: [] };
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

    const data = await response.json();
    return { success: response.ok, message: data.message };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: error.message };
  }
}

export async function updateDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();
    return { success: response.ok, message: data.message };
  } catch (error) {
    console.error("Error updating doctor:", error);
    return { success: false, message: error.message };
  }
}

export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
      method: "DELETE",
    });

    const data = await response.json();
    return { success: response.ok, message: data.message };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: error.message };
  }
}