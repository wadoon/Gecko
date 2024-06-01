package org.gecko.view;

/**
 * Provides methods for getting {@link String}s that are dependent on a given key and the Language currently used in the
 * view.
 */
public interface ResourceHandler {
    String delete = "Delete";
    String set_start_state = "Set as start state";
    String change_kind = "Change kind";
    String open_system = "Open system";
    String undo = "Undo";
    String redo = "Redo";
    String cut = "Cut";
    String copy = "Copy";
    String paste = "Paste";
    String select_all = "Select all";
    String deselect_all = "Deselect all";
    String NEW = "New";
    String open = "Open";
    String save = "Save";
    String save_as = "Save as";
    String IMPORT = "Import";
    String export = "Export";
    String change_view = "Switch view";
    String go_to_parent_system = "Go to parent system";
    String focus_selected_element = "Focus selected element";
    String zoom_in = "Zoom in";
    String zoom_out = "Zoom out";
    String toggle_appearance = "Toggle appearance";
    String search_elements = "Search elements";
    String inspector_add_variable = "Add";
    String inspector_add_contract = "Add";
    String inspector_open_system = "Open";

    String pre_condition = "Pre";
    String post_condition = "Post";
    String invariant = "Inv";
    String contract = "Contract";
    String contract_plural = "Contracts";
    String kind = "Kind";
    String priority = "Priority";
    String color = "Color";
    String type = "Type";
    String input = "Input";
    String output = "Output";
    String visibility = "Visibility";
    String source = "Source";
    String target = "Target";
    String region_plural = "Regions";
    String code = "Code";
    String rename_root_system = "Rename root system";
    String variable_value = "Value";
    String name = "Name";
    String state = "State";
    String region = "Region";
    String pre_condition_short = "Pre";
    String post_condition_short = "Post";
    String invariant_short = "Inv";
    String file = "File";
    String edit = "Edit";
    String view = "View";
    String tools = "Tools";
    String matches_format_string = "%d of %d matches";
    String save_changes_prompt = "Do you want to save your changes?";
    String confirm_exit = "Confirm Exit";
    String search = "Search";

    String automaton = "Automaton";
    String system = "System";

    String title = "Warning";
    String parse_header = "The model has been successfully parsed, but warnings have been emitted";
    String multiple_top_level_header = "Found multiple top level systems. Please choose a system as root of the project.";
    String corrupted_file = "Corrupted file. Could not load project from ";
    String could_not_read_file = "Could not read file: ";
    String could_not_write_file = "Could not write file.";


    String inspector_remove_contract = "Remove the contract from the state";
    String inspector_remove_variable = "Remove the variable from the system";
    String inspector_focus_element = "Focus the element";
    String inspector_selection_backward = "Show the previous selection";
    String inspector_selection_forward = "Show the next selection";
    String switch_view = "Switch view";
    String parent_system = "Go to parent system";


    String cursor = "Cursor Tool";
    String marquee = "Marquee Tool";
    String pan = "Pan Tool";
    String zoom = "Zoom Tool";
    String state_creator = "State Creator Tool";
    String edge_creator = "Edge Creator Tool";
    String region_creator = "Region Creator Tool";
    String system_creator = "System Creator Tool";
    String connection_creator = "Connection Creator Tool";
    String variable_block_creator = "Variable Block Creator Tool";
}
