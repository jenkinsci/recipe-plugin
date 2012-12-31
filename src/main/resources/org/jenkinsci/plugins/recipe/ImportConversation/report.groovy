package org.jenkinsci.plugins.recipe.ImportConversation;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Imported")
    l.header(title:title)
    l.main_panel {
        h1 title


        ul {
            my.reportList.each { r ->
                include r,"report.groovy"
            }
        }

        p {
            a(href:rootURL+'/', _("Go back to the top page"))
        }
    }
}