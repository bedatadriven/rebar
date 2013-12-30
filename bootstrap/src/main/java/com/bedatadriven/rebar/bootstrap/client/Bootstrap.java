package com.bedatadriven.rebar.bootstrap.client;

import com.bedatadriven.rebar.style.client.Source;
import com.bedatadriven.rebar.style.client.Stylesheet;
import com.google.gwt.core.shared.GWT;

@Source({
	"normalize.less",
	"print.less",
	
	// Core CSS
	"scaffolding.less",
	"type.less",
	"code.less",
	"grid.less",
	"tables.less",
	"forms.less",
	"buttons.less",
	
	// Components
	"component-animations.less",
	"glyphicons.less",
	"dropdowns.less",
	"button-groups.less",
	"input-groups.less",
	"navs.less",
	"navbar.less",
	"breadcrumbs.less",
	"pagination.less",
	"pager.less",
	"labels.less",
	"badges.less",
	"jumbotron.less",
	"thumbnails.less",
	"alerts.less",
	"progress-bars.less",
	"media.less",
	"list-group.less",
	"panels.less",
	"wells.less",
	"close.less",
	
	"utilities.less",
	"responsive-utilities.less"

})
public interface Bootstrap extends Stylesheet {

	public static final Bootstrap INSTANCE = GWT.create(Bootstrap.class);
	
	String active();

	String affix();

	String alert();

	@ClassName("alert-danger")
	String alertDanger();

	@ClassName("alert-dismissable")
	String alertDismissable();

	@ClassName("alert-info")
	String alertInfo();

	@ClassName("alert-link")
	String alertLink();

	@ClassName("alert-success")
	String alertSuccess();

	@ClassName("alert-warning")
	String alertWarning();

	String badge();

	String bottom();

	@ClassName("bottom-left")
	String bottomLeft();

	@ClassName("bottom-right")
	String bottomRight();

	String breadcrumb();

	String btn();

	@ClassName("btn-block")
	String btnBlock();

	@ClassName("btn-danger")
	String btnDanger();

	@ClassName("btn-default")
	String btnDefault();

	@ClassName("btn-group")
	String btnGroup();

	@ClassName("btn-group-justified")
	String btnGroupJustified();

	@ClassName("btn-group-lg")
	String btnGroupLg();

	@ClassName("btn-group-sm")
	String btnGroupSm();

	@ClassName("btn-group-vertical")
	String btnGroupVertical();

	@ClassName("btn-group-xs")
	String btnGroupXs();

	@ClassName("btn-info")
	String btnInfo();

	@ClassName("btn-lg")
	String btnLg();

	@ClassName("btn-link")
	String btnLink();

	@ClassName("btn-primary")
	String btnPrimary();

	@ClassName("btn-sm")
	String btnSm();

	@ClassName("btn-success")
	String btnSuccess();

	@ClassName("btn-toolbar")
	String btnToolbar();

	@ClassName("btn-warning")
	String btnWarning();

	@ClassName("btn-xs")
	String btnXs();

	String caption();

	String caret();

	@ClassName("center-block")
	String centerBlock();

	String checkbox();

	@ClassName("checkbox-inline")
	String checkboxInline();

	String clearfix();

	String close();

	@ClassName("col-lg-1")
	String colLg1();

	@ClassName("col-lg-10")
	String colLg10();

	@ClassName("col-lg-11")
	String colLg11();

	@ClassName("col-lg-12")
	String colLg12();

	@ClassName("col-lg-2")
	String colLg2();

	@ClassName("col-lg-3")
	String colLg3();

	@ClassName("col-lg-4")
	String colLg4();

	@ClassName("col-lg-5")
	String colLg5();

	@ClassName("col-lg-6")
	String colLg6();

	@ClassName("col-lg-7")
	String colLg7();

	@ClassName("col-lg-8")
	String colLg8();

	@ClassName("col-lg-9")
	String colLg9();

	@ClassName("col-md-1")
	String colMd1();

	@ClassName("col-md-10")
	String colMd10();

	@ClassName("col-md-11")
	String colMd11();

	@ClassName("col-md-12")
	String colMd12();

	@ClassName("col-md-2")
	String colMd2();

	@ClassName("col-md-3")
	String colMd3();

	@ClassName("col-md-4")
	String colMd4();

	@ClassName("col-md-5")
	String colMd5();

	@ClassName("col-md-6")
	String colMd6();

	@ClassName("col-md-7")
	String colMd7();

	@ClassName("col-md-8")
	String colMd8();

	@ClassName("col-md-9")
	String colMd9();

	@ClassName("col-sm-1")
	String colSm1();

	@ClassName("col-sm-10")
	String colSm10();

	@ClassName("col-sm-11")
	String colSm11();

	@ClassName("col-sm-12")
	String colSm12();

	@ClassName("col-sm-2")
	String colSm2();

	@ClassName("col-sm-3")
	String colSm3();

	@ClassName("col-sm-4")
	String colSm4();

	@ClassName("col-sm-5")
	String colSm5();

	@ClassName("col-sm-6")
	String colSm6();

	@ClassName("col-sm-7")
	String colSm7();

	@ClassName("col-sm-8")
	String colSm8();

	@ClassName("col-sm-9")
	String colSm9();

	@ClassName("col-xs-1")
	String colXs1();

	@ClassName("col-xs-10")
	String colXs10();

	@ClassName("col-xs-11")
	String colXs11();

	@ClassName("col-xs-12")
	String colXs12();

	@ClassName("col-xs-2")
	String colXs2();

	@ClassName("col-xs-3")
	String colXs3();

	@ClassName("col-xs-4")
	String colXs4();

	@ClassName("col-xs-5")
	String colXs5();

	@ClassName("col-xs-6")
	String colXs6();

	@ClassName("col-xs-7")
	String colXs7();

	@ClassName("col-xs-8")
	String colXs8();

	@ClassName("col-xs-9")
	String colXs9();

	@ClassName("col-xs-offset-0")
	String colXsOffset0();

	@ClassName("col-xs-offset-1")
	String colXsOffset1();

	@ClassName("col-xs-offset-10")
	String colXsOffset10();

	@ClassName("col-xs-offset-11")
	String colXsOffset11();

	@ClassName("col-xs-offset-12")
	String colXsOffset12();

	@ClassName("col-xs-offset-2")
	String colXsOffset2();

	@ClassName("col-xs-offset-3")
	String colXsOffset3();

	@ClassName("col-xs-offset-4")
	String colXsOffset4();

	@ClassName("col-xs-offset-5")
	String colXsOffset5();

	@ClassName("col-xs-offset-6")
	String colXsOffset6();

	@ClassName("col-xs-offset-7")
	String colXsOffset7();

	@ClassName("col-xs-offset-8")
	String colXsOffset8();

	@ClassName("col-xs-offset-9")
	String colXsOffset9();

	@ClassName("col-xs-pull-0")
	String colXsPull0();

	@ClassName("col-xs-pull-1")
	String colXsPull1();

	@ClassName("col-xs-pull-10")
	String colXsPull10();

	@ClassName("col-xs-pull-11")
	String colXsPull11();

	@ClassName("col-xs-pull-12")
	String colXsPull12();

	@ClassName("col-xs-pull-2")
	String colXsPull2();

	@ClassName("col-xs-pull-3")
	String colXsPull3();

	@ClassName("col-xs-pull-4")
	String colXsPull4();

	@ClassName("col-xs-pull-5")
	String colXsPull5();

	@ClassName("col-xs-pull-6")
	String colXsPull6();

	@ClassName("col-xs-pull-7")
	String colXsPull7();

	@ClassName("col-xs-pull-8")
	String colXsPull8();

	@ClassName("col-xs-pull-9")
	String colXsPull9();

	@ClassName("col-xs-push-0")
	String colXsPush0();

	@ClassName("col-xs-push-1")
	String colXsPush1();

	@ClassName("col-xs-push-10")
	String colXsPush10();

	@ClassName("col-xs-push-11")
	String colXsPush11();

	@ClassName("col-xs-push-12")
	String colXsPush12();

	@ClassName("col-xs-push-2")
	String colXsPush2();

	@ClassName("col-xs-push-3")
	String colXsPush3();

	@ClassName("col-xs-push-4")
	String colXsPush4();

	@ClassName("col-xs-push-5")
	String colXsPush5();

	@ClassName("col-xs-push-6")
	String colXsPush6();

	@ClassName("col-xs-push-7")
	String colXsPush7();

	@ClassName("col-xs-push-8")
	String colXsPush8();

	@ClassName("col-xs-push-9")
	String colXsPush9();

	String collapse();

	String collapsing();

	String container();

	@ClassName("control-label")
	String controlLabel();

	String disabled();

	String divider();

	String dropdown();

	@ClassName("dropdown-backdrop")
	String dropdownBackdrop();

	@ClassName("dropdown-header")
	String dropdownHeader();

	@ClassName("dropdown-menu")
	String dropdownMenu();

	@ClassName("dropdown-toggle")
	String dropdownToggle();

	String dropup();

	String fade();

	@ClassName("form-control")
	String formControl();

	@ClassName("form-control-static")
	String formControlStatic();

	@ClassName("form-group")
	String formGroup();

	@ClassName("form-horizontal")
	String formHorizontal();

	String h1();

	String h2();

	String h3();

	String h4();

	String h5();

	String h6();

	@ClassName("has-error")
	String hasError();

	@ClassName("has-success")
	String hasSuccess();

	@ClassName("has-warning")
	String hasWarning();

	@ClassName("help-block")
	String helpBlock();

	String hidden();

	@ClassName("hidden-lg")
	String hiddenLg();

	@ClassName("hidden-md")
	String hiddenMd();

	@ClassName("hidden-print")
	String hiddenPrint();

	@ClassName("hidden-sm")
	String hiddenSm();

	@ClassName("hidden-xs")
	String hiddenXs();

	String hide();

	@ClassName("icon-bar")
	String iconBar();

	@ClassName("img-circle")
	String imgCircle();

	@ClassName("img-responsive")
	String imgResponsive();

	@ClassName("img-rounded")
	String imgRounded();

	@ClassName("img-thumbnail")
	String imgThumbnail();

	String in();

	String initialism();

	@ClassName("input-group")
	String inputGroup();

	@ClassName("input-group-addon")
	String inputGroupAddon();

	@ClassName("input-group-btn")
	String inputGroupBtn();

	@ClassName("input-group-lg")
	String inputGroupLg();

	@ClassName("input-group-sm")
	String inputGroupSm();

	@ClassName("input-lg")
	String inputLg();

	@ClassName("input-sm")
	String inputSm();

	String invisible();

	String jumbotron();

	String label();

	@ClassName("label-danger")
	String labelDanger();

	@ClassName("label-default")
	String labelDefault();

	@ClassName("label-info")
	String labelInfo();

	@ClassName("label-primary")
	String labelPrimary();

	@ClassName("label-success")
	String labelSuccess();

	@ClassName("label-warning")
	String labelWarning();

	String lead();

	String left();

	@ClassName("list-group")
	String listGroup();

	@ClassName("list-group-item")
	String listGroupItem();

	@ClassName("list-group-item-heading")
	String listGroupItemHeading();

	@ClassName("list-group-item-text")
	String listGroupItemText();

	@ClassName("list-inline")
	String listInline();

	@ClassName("list-unstyled")
	String listUnstyled();

	String media();

	@ClassName("media-body")
	String mediaBody();

	@ClassName("media-heading")
	String mediaHeading();

	@ClassName("media-list")
	String mediaList();

	@ClassName("media-object")
	String mediaObject();

	String modal();

	@ClassName("modal-backdrop")
	String modalBackdrop();

	@ClassName("modal-body")
	String modalBody();

	@ClassName("modal-content")
	String modalContent();

	@ClassName("modal-dialog")
	String modalDialog();

	@ClassName("modal-footer")
	String modalFooter();

	@ClassName("modal-header")
	String modalHeader();

	@ClassName("modal-open")
	String modalOpen();

	@ClassName("modal-title")
	String modalTitle();

	String nav();

	@ClassName("nav-divider")
	String navDivider();

	@ClassName("nav-justified")
	String navJustified();

	@ClassName("nav-pills")
	String navPills();

	@ClassName("nav-stacked")
	String navStacked();

	@ClassName("nav-tabs")
	String navTabs();

	@ClassName("nav-tabs-justified")
	String navTabsJustified();

	String navbar();

	@ClassName("navbar-brand")
	String navbarBrand();

	@ClassName("navbar-btn")
	String navbarBtn();

	@ClassName("navbar-collapse")
	String navbarCollapse();

	@ClassName("navbar-default")
	String navbarDefault();

	@ClassName("navbar-fixed-bottom")
	String navbarFixedBottom();

	@ClassName("navbar-fixed-top")
	String navbarFixedTop();

	@ClassName("navbar-form")
	String navbarForm();

	@ClassName("navbar-header")
	String navbarHeader();

	@ClassName("navbar-inverse")
	String navbarInverse();

	@ClassName("navbar-link")
	String navbarLink();

	@ClassName("navbar-nav")
	String navbarNav();

	@ClassName("navbar-static-top")
	String navbarStaticTop();

	@ClassName("navbar-text")
	String navbarText();

	@ClassName("navbar-toggle")
	String navbarToggle();

	String next();

	String open();

	@ClassName("page-header")
	String pageHeader();

	String pager();

	String pagination();

	@ClassName("pagination-lg")
	String paginationLg();

	@ClassName("pagination-sm")
	String paginationSm();

	String panel();

	@ClassName("panel-body")
	String panelBody();

	@ClassName("panel-collapse")
	String panelCollapse();

	@ClassName("panel-danger")
	String panelDanger();

	@ClassName("panel-default")
	String panelDefault();

	@ClassName("panel-footer")
	String panelFooter();

	@ClassName("panel-group")
	String panelGroup();

	@ClassName("panel-heading")
	String panelHeading();

	@ClassName("panel-info")
	String panelInfo();

	@ClassName("panel-primary")
	String panelPrimary();

	@ClassName("panel-success")
	String panelSuccess();

	@ClassName("panel-title")
	String panelTitle();

	@ClassName("panel-warning")
	String panelWarning();

	String previous();

	String progress();

	@ClassName("progress-bar")
	String progressBar();

	@ClassName("progress-bar-danger")
	String progressBarDanger();

	@ClassName("progress-bar-info")
	String progressBarInfo();

	@ClassName("progress-bar-success")
	String progressBarSuccess();

	@ClassName("progress-bar-warning")
	String progressBarWarning();

	@ClassName("progress-striped")
	String progressStriped();

	@ClassName("pull-left")
	String pullLeft();

	@ClassName("pull-right")
	String pullRight();

	String radio();

	@ClassName("radio-inline")
	String radioInline();

	String right();

	String row();

	String show();

	String small();

	@ClassName("sr-only")
	String srOnly();

	@ClassName("tab-content")
	String tabContent();

	@ClassName("tab-pane")
	String tabPane();

	String table();

	@ClassName("table-bordered")
	String tableBordered();

	@ClassName("table-responsive")
	String tableResponsive();

	@ClassName("text-center")
	String textCenter();

	@ClassName("text-danger")
	String textDanger();

	@ClassName("text-hide")
	String textHide();

	@ClassName("text-info")
	String textInfo();

	@ClassName("text-left")
	String textLeft();

	@ClassName("text-muted")
	String textMuted();

	@ClassName("text-primary")
	String textPrimary();

	@ClassName("text-right")
	String textRight();

	@ClassName("text-success")
	String textSuccess();

	@ClassName("text-warning")
	String textWarning();

	String thumbnail();

	String tooltip();

	@ClassName("tooltip-arrow")
	String tooltipArrow();

	@ClassName("tooltip-inner")
	String tooltipInner();

	String top();

	@ClassName("top-left")
	String topLeft();

	@ClassName("top-right")
	String topRight();

	@ClassName("visible-lg")
	String visibleLg();

	@ClassName("visible-md")
	String visibleMd();

	@ClassName("visible-print")
	String visiblePrint();

	@ClassName("visible-sm")
	String visibleSm();

	@ClassName("visible-xs")
	String visibleXs();

	String well();

	@ClassName("well-lg")
	String wellLg();

	@ClassName("well-sm")
	String wellSm();
}
