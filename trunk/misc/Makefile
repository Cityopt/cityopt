HTML = CITYOPT_Apros_models.html CITYOPT_Scenario_generators.html README.html DevEnv.html

all: $(HTML)

%.html:	%.md
	multimarkdown -f -t html -o $@ $^

.PHONY: clean
clean:
	rm $(HTML)
