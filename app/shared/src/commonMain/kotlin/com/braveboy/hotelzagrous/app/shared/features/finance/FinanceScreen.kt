package com.braveboy.hotelzagrous.app.shared.features.finance

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braveboy.hotelzagrous.app.shared.features.PersianDatePickerDialog
import com.braveboy.hotelzagrous.core.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(viewModel: FinanceViewModel) {
    val state by viewModel.state.collectAsState()
    var currentTab by remember { mutableStateOf("dashboard") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<FinancialTransaction?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Tab Selector
        ScrollableTabRow(
            selectedTabIndex = when(currentTab) {
                "dashboard" -> 0
                "transactions" -> 1
                "projects" -> 2
                else -> 0
            },
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            Tab(selected = currentTab == "dashboard", onClick = { currentTab = "dashboard" }) {
                Text("داشبورد", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = currentTab == "transactions", onClick = { currentTab = "transactions" }) {
                Text("تراکنش‌ها", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = currentTab == "projects", onClick = { currentTab = "projects" }) {
                Text("وضعیت مالی اتاق‌ها", modifier = Modifier.padding(12.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (currentTab) {
                "dashboard" -> FinanceDashboard(state)
                "transactions" -> TransactionList(
                    state = state,
                    onEdit = { editingTransaction = it; showAddDialog = true },
                    onDelete = { viewModel.onIntent(FinanceIntent.DeleteTransaction(it)) },
                    onFilterChange = { viewModel.onIntent(FinanceIntent.SetTypeFilter(it)) },
                    onSearch = { viewModel.onIntent(FinanceIntent.SetSearchQuery(it)) }
                )
                "projects" -> RoomFinanceList(state)
            }
        }

        FloatingActionButton(
            onClick = { editingTransaction = null; showAddDialog = true },
            modifier = Modifier.align(Alignment.End).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
        }
    }

    if (showAddDialog) {
        AddEditTransactionDialog(
            transaction = editingTransaction,
            rooms = state.rooms,
            onDismiss = { showAddDialog = false },
            onConfirm = {
                viewModel.onIntent(FinanceIntent.UpsertTransaction(it))
                showAddDialog = false
            }
        )
    }
}

@Composable
fun FinanceDashboard(state: FinanceState) {
    val report = state.report
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(
                title = "کل هزینه‌ها",
                amount = report.totalExpenses,
                icon = Icons.Default.TrendingDown,
                color = Color(0xFFE57373),
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "کل دریافتی‌ها",
                amount = report.totalDeposits + report.totalSettlements,
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF81C784),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(
                title = "مانده طلب",
                amount = report.remainingAmount,
                icon = Icons.Default.AccountBalanceWallet,
                color = Color(0xFFFFB74D),
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "سود خالص",
                amount = report.netProfit,
                icon = Icons.Default.MonetizationOn,
                color = Color(0xFF64B5F6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("گزارش نموداری", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Simple Bar Chart Placeholder
        Surface(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val max = listOf(report.totalExpenses, report.totalDeposits, report.totalSettlements, report.netProfit).maxOrNull()?.coerceAtLeast(1) ?: 1
                BarChartItem("هزینه", report.totalExpenses, max, Color(0xFFE57373))
                BarChartItem("بیعانه", report.totalDeposits, max, Color(0xFF81C784))
                BarChartItem("تسویه", report.totalSettlements, max, Color(0xFF4CAF50))
                BarChartItem("سود", report.netProfit, max, Color(0xFF64B5F6))
            }
        }
    }
}

@Composable
fun BarChartItem(label: String, value: Long, max: Long, color: Color) {
    val heightFactor = (value.toFloat() / max.toFloat()).coerceIn(0.1f, 1f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(0.8f * heightFactor)
                .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        )
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun DashboardCard(title: String, amount: Long, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "${amount.toString()} ریال",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TransactionList(
    state: FinanceState,
    onEdit: (FinancialTransaction) -> Unit,
    onDelete: (String) -> Unit,
    onFilterChange: (TransactionType?) -> Unit,
    onSearch: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearch,
                placeholder = { Text("جستجو در عنوان...") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            var expanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(
                    selected = state.typeFilter != null,
                    onClick = { expanded = true },
                    label = { Text(state.typeFilter?.name ?: "همه انواع") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("همه") }, onClick = { onFilterChange(null); expanded = false })
                    TransactionType.entries.forEach { type ->
                        DropdownMenuItem(text = { Text(type.name) }, onClick = { onFilterChange(type); expanded = false })
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val filteredTransactions = state.report.transactions.filter { tx ->
            (state.typeFilter == null || tx.transactionType == state.typeFilter) &&
            (state.searchQuery.isBlank() || tx.title.contains(state.searchQuery, ignoreCase = true))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredTransactions) { tx ->
                TransactionItem(tx, onEdit, onDelete)
            }
        }
    }
}

@Composable
fun TransactionItem(tx: FinancialTransaction, onEdit: (FinancialTransaction) -> Unit, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val color = when(tx.transactionType) {
                TransactionType.EXPENSE -> Color(0xFFE57373)
                TransactionType.DEPOSIT -> Color(0xFF81C784)
                TransactionType.SETTLEMENT -> Color(0xFF4CAF50)
            }
            
            Box(modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(
                    when(tx.transactionType) {
                        TransactionType.EXPENSE -> Icons.Default.Remove
                        else -> Icons.Default.Add
                    },
                    contentDescription = null,
                    tint = color
                )
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, fontWeight = FontWeight.Bold)
                Text("${tx.paymentDate} - ${tx.paymentMethod}", style = MaterialTheme.typography.labelSmall)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("${tx.amount} ریال", fontWeight = FontWeight.ExtraBold, color = color)
                Row {
                    IconButton(onClick = { onEdit(tx) }) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                    IconButton(onClick = { onDelete(tx.id) }) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun RoomFinanceList(state: FinanceState) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(state.roomSummaries) { summary ->
            RoomFinanceItem(summary)
        }
    }
}

@Composable
fun RoomFinanceItem(summary: RoomFinancialSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("اتاق ${summary.roomNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(summary.guestName, style = MaterialTheme.typography.titleMedium)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            FinanceRow("مبلغ قرارداد", summary.totalContractAmount)
            FinanceRow("مجموع بیعانه‌ها", summary.totalDeposits, color = Color(0xFF81C784))
            FinanceRow("مجموع هزینه‌ها", summary.totalExpenses, color = Color(0xFFE57373))
            FinanceRow("مانده تسویه", summary.remainingSettlement, fontWeight = FontWeight.ExtraBold, color = if(summary.remainingSettlement > 0) Color(0xFFFFB74D) else Color(0xFF4CAF50))
            
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("سود پروژه", fontWeight = FontWeight.Bold)
                Text("${summary.profit} ریال", fontWeight = FontWeight.Bold, color = if(summary.profit >= 0) Color(0xFF4CAF50) else Color(0xFFE57373))
            }
        }
    }
}

@Composable
fun FinanceRow(label: String, amount: Long, fontWeight: FontWeight = FontWeight.Normal, color: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text("${amount} ریال", style = MaterialTheme.typography.bodySmall, fontWeight = fontWeight, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionDialog(
    transaction: FinancialTransaction?,
    rooms: List<Room>,
    onDismiss: () -> Unit,
    onConfirm: (FinancialTransaction) -> Unit
) {
    var title by remember { mutableStateOf(transaction?.title ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var type by remember { mutableStateOf(transaction?.transactionType ?: TransactionType.EXPENSE) }
    var selectedRoom by remember { mutableStateOf(rooms.find { it.id == transaction?.roomId } ?: rooms.firstOrNull()) }
    var date by remember { mutableStateOf(transaction?.paymentDate ?: "") }
    var method by remember { mutableStateOf(transaction?.paymentMethod ?: PaymentMethod.CASH) }
    var status by remember { mutableStateOf(transaction?.status ?: TransactionStatus.PAID) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedRoom by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (transaction == null) "ثبت تراکنش جدید" else "ویرایش تراکنش") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Room Selection
                Box {
                    OutlinedTextField(
                        value = selectedRoom?.let { "اتاق ${it.roomNumber} - ${it.guestName}" } ?: "انتخاب اتاق",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("اتاق مربوطه") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { IconButton(onClick = { expandedRoom = true }) { Icon(Icons.Default.ArrowDropDown, null) } }
                    )
                    DropdownMenu(expanded = expandedRoom, onDismissRequest = { expandedRoom = false }) {
                        rooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text("اتاق ${room.roomNumber} - ${room.guestName}") },
                                onClick = { selectedRoom = room; expandedRoom = false }
                            )
                        }
                    }
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("مبلغ (ریال)") }, modifier = Modifier.fillMaxWidth())
                
                // Transaction Type
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TransactionType.entries.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("تاریخ") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.CalendarToday, null) } }
                )

                // Status & Method
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("روش پرداخت", style = MaterialTheme.typography.labelSmall)
                        PaymentMethod.entries.forEach { m ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = method == m, onClick = { method = m })
                                Text(m.name, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("وضعیت", style = MaterialTheme.typography.labelSmall)
                        TransactionStatus.entries.forEach { s ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = status == s, onClick = { status = s })
                                Text(s.name, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedRoom != null && title.isNotBlank() && amount.toLongOrNull() != null) {
                    onConfirm(
                        FinancialTransaction(
                            id = transaction?.id ?: "",
                            roomId = selectedRoom!!.id,
                            title = title,
                            amount = amount.toLongOrNull() ?: 0,
                            transactionType = type,
                            paymentDate = date,
                            paymentMethod = method,
                            status = status,
                            description = description
                        )
                    )
                }
            }) { Text("تایید") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )

    if (showDatePicker) {
        PersianDatePickerDialog(
            initialDate = date,
            onDateSelected = { d, _ -> date = d; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}
