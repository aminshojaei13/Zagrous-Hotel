package com.braveboy.hotelzagrous.app.shared.features.finance

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
        val selectedIndex = when(currentTab) {
            "dashboard" -> 0
            "transactions" -> 1
            "projects" -> 2
            else -> 0
        }
        
        SecondaryTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            Tab(selected = currentTab == "dashboard", onClick = { currentTab = "dashboard" }) {
                Text("داشبورد", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleSmall)
            }
            Tab(selected = currentTab == "transactions", onClick = { currentTab = "transactions" }) {
                Text("تراکنش‌ها", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleSmall)
            }
            Tab(selected = currentTab == "projects", onClick = { currentTab = "projects" }) {
                Text("وضعیت مالی اتاق‌ها", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleSmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
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
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "کل دریافتی‌ها",
                amount = report.totalDeposits + report.totalSettlements,
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(
                title = "مانده طلب",
                amount = report.remainingAmount,
                icon = Icons.Default.AccountBalanceWallet,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "سود خالص",
                amount = report.netProfit,
                icon = Icons.Default.MonetizationOn,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("گزارش نموداری", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(24.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val values = listOf(report.totalExpenses, report.totalDeposits, report.totalSettlements, report.netProfit)
                val max = values.maxOrNull()?.coerceAtLeast(1) ?: 1
                BarChartItem("هزینه", report.totalExpenses, max, MaterialTheme.colorScheme.error)
                BarChartItem("بیعانه", report.totalDeposits, max, Color(0xFF81C784))
                BarChartItem("تسویه", report.totalSettlements, max, Color(0xFF4CAF50))
                BarChartItem("سود", report.netProfit, max, MaterialTheme.colorScheme.primary)
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun BarChartItem(label: String, value: Long, max: Long, color: Color) {
    val heightFactor = (value.toFloat() / max.toFloat()).coerceIn(0.05f, 1f)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
        Text(
            text = value.formatPrice(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(32.dp)
                .fillMaxHeight(0.8f * heightFactor)
                .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        )
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DashboardCard(title: String, amount: Long, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                color = color.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "${amount.formatPrice()} ریال",
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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearch,
                placeholder = { Text("جستجو در عنوان...") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            var expanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(
                    selected = state.typeFilter != null,
                    onClick = { expanded = true },
                    label = { 
                        Text(
                            when(state.typeFilter) {
                                TransactionType.EXPENSE -> "هزینه"
                                TransactionType.DEPOSIT -> "بیعانه"
                                TransactionType.SETTLEMENT -> "تسویه"
                                null -> "همه انواع"
                            }
                        ) 
                    },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    shape = RoundedCornerShape(12.dp)
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("همه") }, onClick = { onFilterChange(null); expanded = false })
                    DropdownMenuItem(text = { Text("هزینه") }, onClick = { onFilterChange(TransactionType.EXPENSE); expanded = false })
                    DropdownMenuItem(text = { Text("بیعانه") }, onClick = { onFilterChange(TransactionType.DEPOSIT); expanded = false })
                    DropdownMenuItem(text = { Text("تسویه") }, onClick = { onFilterChange(TransactionType.SETTLEMENT); expanded = false })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val filteredTransactions = state.report.transactions.filter { tx ->
            (state.typeFilter == null || tx.transactionType == state.typeFilter) &&
            (state.searchQuery.isBlank() || tx.title.contains(state.searchQuery, ignoreCase = true))
        }

        if (filteredTransactions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("تراکنشی یافت نشد", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(filteredTransactions) { tx ->
                    TransactionItem(tx, onEdit, onDelete)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: FinancialTransaction, onEdit: (FinancialTransaction) -> Unit, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val (color, icon) = when(tx.transactionType) {
                TransactionType.EXPENSE -> MaterialTheme.colorScheme.error to Icons.Default.Remove
                TransactionType.DEPOSIT -> Color(0xFF81C784) to Icons.Default.Add
                TransactionType.SETTLEMENT -> Color(0xFF4CAF50) to Icons.Default.DoneAll
            }
            
            Surface(
                modifier = Modifier.size(44.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(10.dp))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${tx.paymentDate} • ${when(tx.paymentMethod) {
                        PaymentMethod.CASH -> "نقدی"
                        PaymentMethod.CARD -> "کارت"
                        PaymentMethod.BANK_TRANSFER -> "حواله"
                    }}", 
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${tx.amount.formatPrice()} ریال", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = color,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = { onEdit(tx) }, modifier = Modifier.size(32.dp)) { 
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) 
                    }
                    IconButton(onClick = { onDelete(tx.id) }, modifier = Modifier.size(32.dp)) { 
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) 
                    }
                }
            }
        }
    }
}

@Composable
fun RoomFinanceList(state: FinanceState) {
    if (state.roomSummaries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("اطلاعاتی برای نمایش وجود ندارد", color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
            items(state.roomSummaries) { summary ->
                RoomFinanceItem(summary)
            }
        }
    }
}

@Composable
fun RoomFinanceItem(summary: RoomFinancialSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Hotel, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("اتاق ${summary.roomNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                Text(summary.guestName, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            FinanceRow("مبلغ قرارداد", summary.totalContractAmount)
            FinanceRow("مجموع بیعانه‌ها", summary.totalDeposits, color = Color(0xFF4CAF50))
            FinanceRow("مجموع هزینه‌ها", summary.totalExpenses, color = MaterialTheme.colorScheme.error)
            
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    FinanceRow(
                        "مانده تسویه", 
                        summary.remainingSettlement, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = if(summary.remainingSettlement > 0) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.height(4.dp))
                    FinanceRow(
                        "سود پروژه", 
                        summary.profit, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = if(summary.profit >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceRow(label: String, amount: Long, fontWeight: FontWeight = FontWeight.Normal, color: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("${amount.formatPrice()} ریال", style = MaterialTheme.typography.bodyMedium, fontWeight = fontWeight, color = color)
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
        title = { Text(if (transaction == null) "ثبت تراکنش جدید" else "ویرایش تراکنش", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Room Selection
                Column {
                    Text("اتاق مربوطه", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Box {
                        OutlinedCard(
                            onClick = { expandedRoom = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hotel, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = selectedRoom?.let { "اتاق ${it.roomNumber} - ${it.guestName}" } ?: "انتخاب اتاق",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                        DropdownMenu(
                            expanded = expandedRoom, 
                            onDismissRequest = { expandedRoom = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            rooms.forEach { room ->
                                DropdownMenuItem(
                                    text = { Text("اتاق ${room.roomNumber} - ${room.guestName}") },
                                    onClick = { selectedRoom = room; expandedRoom = false }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("عنوان تراکنش") }, 
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = amount, 
                    onValueChange = { amount = it }, 
                    label = { Text("مبلغ (ریال)") }, 
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                
                // Transaction Type
                Column {
                    Text("نوع تراکنش", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TransactionType.entries.forEach { t ->
                            val label = when(t) {
                                TransactionType.EXPENSE -> "هزینه"
                                TransactionType.DEPOSIT -> "بیعانه"
                                TransactionType.SETTLEMENT -> "تسویه"
                            }
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("تاریخ پرداخت") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.CalendarToday, null) } }
                )

                // Status & Method
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("روش پرداخت", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        PaymentMethod.entries.forEach { m ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = method == m, onClick = { method = m })
                                Text(
                                    when(m) {
                                        PaymentMethod.CASH -> "نقدی"
                                        PaymentMethod.CARD -> "کارت"
                                        PaymentMethod.BANK_TRANSFER -> "حواله"
                                    }, 
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("وضعیت", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        TransactionStatus.entries.forEach { s ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = status == s, onClick = { status = s })
                                Text(
                                    if(s == TransactionStatus.PAID) "پرداخت شده" else "در انتظار", 
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedRoom != null && title.isNotBlank() && amount.toLongOrNull() != null) {
                        onConfirm(
                            FinancialTransaction(
                                id = transaction?.id ?: "",
                                roomId = selectedRoom!!.id,
                                title = title,
                                amount = amount.normalizeDigits().toLongOrNull() ?: 0,
                                transactionType = type,
                                paymentDate = date,
                                paymentMethod = method,
                                status = status,
                                description = description
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) { Text("ذخیره تراکنش") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
        shape = RoundedCornerShape(24.dp)
    )

    if (showDatePicker) {
        PersianDatePickerDialog(
            initialDate = date,
            onDateSelected = { d, _ -> date = d; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}
