const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ADMIN_ACTOR = "admin_local";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Catalog & public settings: live reads/writes work without Firebase Auth while admin_config/admins stay locked", async () => {
  const created = new Date(Date.now() - 10000);
  const updated = new Date(Date.now() - 1000);
  const db = testEnv.unauthenticatedContext().firestore();

  const batch = db.batch();
  batch.set(db.collection("batches").doc("parmar_4_0"), {
    name: "PARMAR 4.0",
    subtitle: "BY PARMAR SIR • GK & GS COMPLETE",
    sortOrder: 0,
    createdBy: ADMIN_ACTOR,
    createdAt: created,
    updatedAt: created,
  });
  batch.set(db.collection("subjects").doc("parmar_4_0_biology"), {
    batchId: "parmar_4_0",
    name: "BIOLOGY",
    sortOrder: 0,
    createdBy: ADMIN_ACTOR,
    createdAt: created,
    updatedAt: created,
  });
  batch.set(db.collection("settings").doc("public"), {
    whatsappUrl: "https://whatsapp.com",
    telegramUrl: "https://telegram.org",
    seededDefaults: true,
    updatedBy: ADMIN_ACTOR,
    createdAt: created,
    updatedAt: created,
  });
  await assertSucceeds(batch.commit());

  await assertSucceeds(
    db.collection("lectures").doc("lec_1").set({
      batchId: "parmar_4_0",
      subjectId: "parmar_4_0_biology",
      title: "CELL - LEC 1",
      videoUrl: "https://youtube.com/watch?v=dQw4w9WgXcQ",
      engNotesUrl: "https://drive.google.com/file/d/abc/view",
      hinNotesUrl: "",
      pptUrl: "",
      createdBy: ADMIN_ACTOR,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    db.collection("settings").doc("public").update({
      whatsappUrl: "https://wa.me/919876543210",
      telegramUrl: "https://t.me/studyaspirant",
      updatedBy: ADMIN_ACTOR,
      updatedAt: updated,
    })
  );

  await assertSucceeds(db.collection("batches").get());
  await assertSucceeds(db.collection("subjects").get());
  await assertSucceeds(db.collection("lectures").get());
  await assertSucceeds(db.collection("settings").doc("public").get());
  await assertSucceeds(db.collection("lectures").doc("lec_1").delete());

  // Legacy admin docs and settings/admin_config remain locked
  await assertFails(db.collection("settings").doc("admin_config").get());
  await assertFails(db.collection("settings").doc("admin_config").set({ adminPasscodeHash: "x" }));
  await assertFails(db.collection("admins").doc("any").get());
  await assertFails(db.collection("settings").doc("public").delete());
});

test("Student profiles: direct registration & name update succeed while PII reads/invalid phones/ghost fields are blocked", async () => {
  const db = testEnv.unauthenticatedContext().firestore();
  const created = new Date(Date.now() - 10000);
  const updated = new Date(Date.now() - 1000);

  const stuRef = db.collection("students").doc("stu_9876543210_abcd1234");
  await assertSucceeds(
    stuRef.set({
      fullName: "Aarav Kumar",
      phoneNumber: "9876543210",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    stuRef.update({
      fullName: "Aarav Kumar Sharma",
      updatedAt: updated,
    })
  );

  // PII reads/list/delete remain blocked
  await assertFails(stuRef.get());
  await assertFails(db.collection("students").get());
  await assertFails(stuRef.delete());

  // Reject invalid phone format, immutable phoneNumber change, or ghost field
  await assertFails(
    db.collection("students").doc("stu_bad").set({
      fullName: "Bad Phone",
      phoneNumber: "12345",
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertFails(
    stuRef.update({
      phoneNumber: "9999999999",
      updatedAt: updated,
    })
  );
  await assertFails(
    db.collection("students").doc("stu_ghost").set({
      fullName: "Ghost User",
      phoneNumber: "9876543211",
      isAdmin: true,
      createdAt: created,
      updatedAt: created,
    })
  );
});

test("Validation guards: reject extra @DocumentId, orphaned lectures, and future timestamps", async () => {
  const db = testEnv.unauthenticatedContext().firestore();
  const created = new Date(Date.now() - 10000);

  await assertFails(
    db.collection("batches").doc("b2").set({
      id: "b2",
      name: "EXTRA ID",
      subtitle: "GK",
      sortOrder: 1,
      createdBy: ADMIN_ACTOR,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertFails(
    db.collection("batches").doc("b3").set({
      name: "FUTURE",
      subtitle: "GK",
      sortOrder: 1,
      createdBy: ADMIN_ACTOR,
      createdAt: new Date(Date.now() + 60000),
      updatedAt: created,
    })
  );

  await assertFails(
    db.collection("lectures").doc("orphan_lec").set({
      batchId: "nonexistent_batch",
      subjectId: "nonexistent_subject",
      title: "ORPHAN",
      videoUrl: "",
      engNotesUrl: "",
      hinNotesUrl: "",
      pptUrl: "",
      createdBy: ADMIN_ACTOR,
      createdAt: created,
      updatedAt: created,
    })
  );
});
