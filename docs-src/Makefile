.PHONY: help build server server-with-drafts clean all hugo

examples_src := $(wildcard content/example/*.md)
examples := $(patsubst content/example/%.md,../docs/example/%/index.html,$(examples_src))

help: ## Print this help text
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

server: ## Build and run a local web server
	hugo server -d ../docs

server-with-drafts: ## Run hugo server and include drafts
	hugo server -D -d ../docs

clean: ## Clean /docs
	rm -rf ../docs

hugo: $(examples) ## Build the site and place output in ../docs
all: $(examples)

print: $(examples)
	echo $?

../docs/index.html: content/index.md
	hugo -d ../docs

../docs/%/index.html: content/%/index.md
	hugo -d ../docs

../docs/example/%/index.html: content/example/%.md
	hugo -d ../docs

# Catch all is static content
../docs/%: static/%
	hugo -d ../docs
