package org.jenkinsci.plugins.recipe.ImportWizard

import hudson.Functions
import java.text.DateFormat;

def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)

l.layout {
    def title = _("Import Recipe")
    l.header(title:title)
    l.main_panel {
        h1 {
            img(src:my.iconFileName)
            text(" "+title)
        }

        set("page","available")
        include(my,"tabBar")

        style """
.recipe-info {
    color: #888;
    display: inline-block;
    margin-left: 2em;
}
.recipe-import-button {
    vertical-align: middle;
    width: 1px; /* as small as possible */
}
.excerpt {
    margin-left: 2em;
    margin-top: 0.5em;
    margin-bottom: 0.5em;
}
"""

        table(style:"margin-top:0px; border-top:0px;",class:"sortable pane bigtable") {
            tr(style:"border-top:0px") {
                th(colspan:2, " ")
            }
            def fmt = DateFormat.getDateInstance(DateFormat.DEFAULT, Functions.currentLocale);
            my.catalog.recipes.each { r ->
                tr {
                    td(class:"pane recipe-import-button") {
                        form (method:"POST",action:"catalog/recipe/${r.id}/import") {
                            f.submit(value:"Import")
                        }
                    }
                    td(class:"pane recipe-title") {
                        b (r.displayName)
                        div (class:"recipe-info") {
                            text "ver.${r.version} by ${r.author} on ${fmt.format(r.timestamp)}"
                        }
                        div (class:"excerpt", r.description)
                    }
                }

/*                tr {
                    td(class:"pane", "ver.${r.version}")
                    td(class:"pane", "by ${r.author}")
                    td(class:"pane", "on ${fmt.format(r.timestamp)}")
                    td(class:"pane") {
                    }
                }
                tr {
                    td(class:"pane", colspan:4, r.description)
                }*/
            }
        }
    }
}