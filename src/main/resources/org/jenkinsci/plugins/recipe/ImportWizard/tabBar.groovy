package org.jenkinsci.plugins.recipe.ImportWizard;

def l = namespace(lib.LayoutTagLib)

l.tabBar {
    l.tab(name:_("Available"), active:page=="available", href:".")
    l.tab(name:_("Advanced"), active:page=="advanced", href:"advanced")
}