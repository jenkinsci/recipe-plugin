package org.jenkinsci.plugins.recipe.ImportWizard;

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

        set("page","advanced")
        include(my,"tabBar")

        table(style:"margin-top:0px; border-top:0px;",class:"sortable pane bigtable") {
            tr(style:"border-top:0px") {
                th(" ")
            }
            tr {
                td(class:"pane") {
                    h1 "Install from URL"
                    f.form(action:"retrieve",method:"POST") {
                        f.entry(title:_("Recipe URL")) {
                            f.textbox(name:"url")
                        }
                        f.block {
                            f.submit(value:_("Next"))
                        }
                    }
                }
            }
        }
    }
}