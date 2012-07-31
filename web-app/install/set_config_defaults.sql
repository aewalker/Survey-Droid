USE survey_droid;

INSERT INTO sd_configurations (c_key, opt, c_value) VALUES
	("https", "==", "1"),
	("scheduler_interval", "==", "1440"),
	("push_interval", "==", "20"),
	("pull_interval", "==", "1440"),
	("server", "==", "survey-droid.org"),
	("device_enabled", "==", "1"),
	("location_interval", "==", "15"),
	("admin_phone_number", "==", "2125550199"),
	("admin_name", "==", "Admin"),
	("allow_blank_free_response", "==", "0"),
	("allow_no_choices", "==", "0"),
	("show_survey_name", "==", "1"),
	("voice_format", "==", "mpeg4"),

	("features_enabled.survey", "==", "1"),
	("features_enabled.callog", "==", "1"),
	("features_enabled.location", "==", "1");