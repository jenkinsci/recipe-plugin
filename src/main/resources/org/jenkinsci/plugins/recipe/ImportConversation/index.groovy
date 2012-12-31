package org.jenkinsci.plugins.recipe.ImportConversation

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)

/*
    The first step: show what's in the recipe and ask the user a confirmation.
 */
l.layout {
    def title = _("Importing ${my.recipe.displayName}")
    l.header(title:title)
    l.main_panel {
        h1 title

        p _("blurb")

        h3 _("Description")
        p my.recipe.description

        def plugins = my.pluginsThatRequireAttention;

        if (!plugins.isEmpty()) {
            h2 _("Plugins that are needed")
            div(class:"warning", _("withoutPluginRecipeMightNotWork"))
            f.form(action:"applyPlugins",method:"POST") {
                plugins.each { pi ->
                    if (pi.installSource!=null) {
                        if (pi.current==null) {
                            f.entry {
                                f.checkbox(name:"plugin."+pi.name, title:_("action.installPlugin",pi.installSource.displayName), checked:true)
                            }
                        } else {
                            f.entry {
                                f.checkbox(name:"plugin."+pi.name, title:_("action.updatePlugin", pi.installSource.displayName,
                                        pi.installSource.version, pi.version, pi.current.version), checked:true)
                            }
                        }
                    } else {
                        f.entry {
                            div(class:"error", _("action.notInstallable",pi.name))
                        }
                    }
                }
                f.block {// TODO: switch to bottomButtonBar with new Jenkins
                    f.submit(name:"apply",value:_("Install/update plugins"))
                    f.submit(name:"skip",value:_("proceedAnyway"))
                }
            }
        } else {
            h2 _("Contents to be imported")
            f.form(action:"cook",method:"POST") {
                int n=0;
                my.recipe.ingredients.findAll { it.isVisibleDuringImport() }.each { i ->
                    f.section(name:"ingredient${n++}",title:i.descriptor.displayName) {
                        context.setVariable("instance",i)
                        context.setVariable("descriptor",i.descriptor)
                        include i,"import"
                    }
                }
                f.block { // TODO: switch to bottomButtonBar with new Jenkins
                    f.submit(value:_("Import"))
                }
            }
        }
    }
}
