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
                    h1 _("Install from URL")
                    f.form(action:"retrieve",method:"POST") {
                        f.entry(title:_("Recipe URL")) {
                            f.textbox(name:"url")
                        }
                        f.block {
                            f.submit(value:_("Next"))
                        }
                    }

                    h1 _("Upload from disk")
                    f.form(action:"upload",method:"POST",enctype:"multipart/form-data") {
                        f.entry(title:_("Recipe File")) {
                            input (type:"file", name:"file", class:"setting-input", size:40)
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