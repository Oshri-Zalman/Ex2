Ex2 Project:

Overview
This project implements a simple spreadsheet system similar to Excel, supporting basic cell operations including numeric values, text, and formulas. The implementation handles cell references, basic arithmetic operations, and error detection (including cyclic references).
Main Components
SCell (Spreadsheet Cell)

Represents individual cells in the spreadsheet
Handles different types of cell content:

Numbers (e.g., "123", "-45.67")
Text (e.g., "Hello")
Formulas (e.g., "=A1+B2", "=5*3")


Manages error detection and formula evaluation

CellEntry

Handles cell coordinate parsing and validation
Converts between spreadsheet notation (e.g., "A1", "B2") and array indices
Validates cell references to ensure they're within range

Ex2Sheet

Main spreadsheet implementation
Manages the 2D array of cells
Handles operations like:

Cell value setting and getting
Formula evaluation
Cell reference resolution
Save/load functionality



Features

Basic arithmetic operations (+, -, *, /)
Cell references in formulas
Error handling for:

Cyclic references (e.g., A1=A1)
Invalid formulas
Out of range references


File I/O support for saving/loading spreadsheets

Formula Syntax

Formulas must start with "="
Supported operations: +, -, *, /
Valid cell references: A1, B2, etc.
Examples:

=1+2
=A1*B2
=(C1+D1)/2



Error Types

ERR_FORM: Invalid formula syntax
ERR_CYCLE: Cyclic reference detected
